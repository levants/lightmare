/*
 * Lightmare, Lightweight embedded EJB container (works for stateless session beans) with JPA / Hibernate support
 *
 * Copyright (c) 2013, Levan Tsinadze, or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.lightmare.deploy.fs;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.lightmare.cache.ConnectionContainer;
import org.lightmare.cache.DeploymentDirectory;
import org.lightmare.cache.MetaContainer;
import org.lightmare.cache.RestContainer;
import org.lightmare.config.Configuration;
import org.lightmare.jpa.datasource.FileParsers;
import org.lightmare.jpa.datasource.Initializer;
import org.lightmare.rest.providers.RestProvider;
import org.lightmare.utils.ObjectUtils;
import org.lightmare.utils.collections.CollectionUtils;
import org.lightmare.utils.concurrent.ThreadFactoryUtil;
import org.lightmare.utils.fs.WatchUtils;
import org.lightmare.utils.logging.LogUtils;

/**
 * Deployment manager, {@link Watcher#deployFile(URL)},
 * {@link Watcher#undeployFile(URL)}, {@link Watcher#listDeployments()} and
 * {@link File} modification event handler for deployments if java version is
 * 1.7 or above
 *
 * @author Levan Tsinadze
 * @since 0.0.45
 */
public class Watcher implements Runnable {

    // Name of deployment watch service thread
    private static final String DEPLOY_THREAD_NAME = "watch_thread";

    // Priority of deployment watch service thread
    private static final int DEPLOY_POOL_PRIORITY = Thread.MAX_PRIORITY - 5;

    // Sleep time of thread between watch service status scans
    private static final long SLEEP_TIME = 5500L;

    // Thread pool for watch service threads
    private static final ExecutorService DEPLOY_POOL = Executors
            .newSingleThreadExecutor(new ThreadFactoryUtil(DEPLOY_THREAD_NAME, DEPLOY_POOL_PRIORITY));

    // Sets of directories of application deployments
    private Set<DeploymentDirectory> deployments;

    // Sets of data source descriptor file paths
    private Set<String> dataSources;

    // Zero / default status for watch service
    private static final int ZERO_WATCH_STATUS = 0;

    // Error code for java main process exit
    private static final int ERROR_EXIT = -1;

    // Messages
    private static final String MODIFY_MESSAGE = "Modify: %s, count: %s\n";

    private static final String DELETE_MESSAGE = "Delete: %s, count: %s\n";

    private static final String CREATE_MESSAGE = "Create: %s, count: %s\n";

    private static final Logger LOG = Logger.getLogger(Watcher.class);

    /**
     * Defines file types for watch service
     *
     * @author Levan Tsinadze
     * @since 0.0.45
     */
    private static enum WatchFileType {

        DATA_SOURCE, DEPLOYMENT, NONE;
    }

    /**
     * To filter only deployed sub files from directory
     *
     * @author Levan Tsinadze
     * @since 0.0.45
     */
    private static class DeployFiletr implements FileFilter {

        @Override
        public boolean accept(File file) {

            boolean accept;

            try {
                URL url = file.toURI().toURL();
                url = WatchUtils.clearURL(url);
                accept = MetaContainer.chackDeployment(url);
            } catch (MalformedURLException ex) {
                LOG.error(ex.getMessage(), ex);
                accept = false;
            } catch (IOException ex) {
                LOG.error(ex.getMessage(), ex);
                accept = false;
            }

            return accept;
        }
    }

    private Watcher() {
        deployments = getDeployDirectories();
        dataSources = getDataSourcePaths();
    }

    /**
     * Clears and gets file {@link URL} by file name
     *
     * @param fileName
     * @return {@link URL}
     * @throws IOException
     */
    private static URL getAppropriateURL(String fileName) throws IOException {

        URL url;

        File file = new File(fileName);
        url = file.toURI().toURL();
        url = WatchUtils.clearURL(url);

        return url;
    }

    /**
     * Gets {@link Set} of {@link DeploymentDirectory} instances from
     * configuration
     *
     * @return {@link Set}<code><DeploymentDirectory></code>
     */
    private static Set<DeploymentDirectory> getDeployDirectories() {

        Set<DeploymentDirectory> deploymetDirss = new HashSet<DeploymentDirectory>();

        Collection<Configuration> configs = MetaContainer.CONFIGS.values();
        Set<DeploymentDirectory> deploymetDirssCurrent;
        for (Configuration config : configs) {
            deploymetDirssCurrent = config.getDeploymentPath();
            if (config.isWatchStatus() && CollectionUtils.valid(deploymetDirssCurrent)) {
                deploymetDirss.addAll(deploymetDirssCurrent);
            }
        }

        return deploymetDirss;
    }

    /**
     * Gets {@link Set} of data source paths from configuration
     *
     * @return {@link Set}<code><String></code>
     */
    private static Set<String> getDataSourcePaths() {

        Set<String> paths = new HashSet<String>();

        Collection<Configuration> configs = MetaContainer.CONFIGS.values();
        Set<String> pathsCurrent;
        for (Configuration config : configs) {
            pathsCurrent = config.getDataSourcePath();
            if (config.isWatchStatus() && CollectionUtils.valid(pathsCurrent)) {
                paths.addAll(pathsCurrent);
            }
        }

        return paths;
    }

    private static WatchFileType checkType(Set<DeploymentDirectory> apps, String parentPath) {

        WatchFileType type;

        String deploymantPath;
        Iterator<DeploymentDirectory> iterator = apps.iterator();
        boolean notDeployment = Boolean.TRUE;
        DeploymentDirectory deployment;
        while (iterator.hasNext() && notDeployment) {
            deployment = iterator.next();
            deploymantPath = deployment.getPath();
            notDeployment = ObjectUtils.notEquals(deploymantPath, parentPath);
        }

        if (notDeployment) {
            type = WatchFileType.NONE;
        } else {
            type = WatchFileType.DEPLOYMENT;
        }

        return type;
    }

    /**
     * Checks and gets appropriated {@link WatchFileType} by passed file name
     *
     * @param fileName
     * @return {@link WatchFileType}
     */
    private static WatchFileType checkType(String fileName) {

        WatchFileType type;

        File file = new File(fileName);
        String path = file.getPath();
        String filePath = WatchUtils.clearPath(path);
        path = file.getParent();
        String parentPath = WatchUtils.clearPath(path);

        Set<DeploymentDirectory> apps = getDeployDirectories();
        Set<String> dss = getDataSourcePaths();
        if (CollectionUtils.valid(apps)) {
            type = checkType(apps, parentPath);
        } else if (CollectionUtils.valid(dss) && dss.contains(filePath)) {
            type = WatchFileType.DATA_SOURCE;
        } else {
            type = WatchFileType.NONE;
        }

        return type;
    }

    /**
     * Fills passed {@link List} of {@link File}s by passed {@link File} array
     *
     * @param files
     * @param list
     */
    private static void fillFileList(File[] files, List<File> list) {

        if (CollectionUtils.valid(files)) {
            for (File file : files) {
                list.add(file);
            }
        }
    }

    /**
     * Lists all deployed {@link File}s
     *
     * @return {@link List}<File>
     */
    public static List<File> listDeployments() {

        List<File> list = new ArrayList<File>();

        Collection<Configuration> configs = MetaContainer.CONFIGS.values();
        Set<DeploymentDirectory> deploymetDirss = new HashSet<DeploymentDirectory>();
        Set<DeploymentDirectory> deploymetDirssCurrent;
        for (Configuration config : configs) {
            deploymetDirssCurrent = config.getDeploymentPath();
            if (CollectionUtils.valid(deploymetDirssCurrent)) {
                deploymetDirss.addAll(deploymetDirssCurrent);
            }
        }

        File[] files;
        if (CollectionUtils.valid(deploymetDirss)) {
            String path;
            DeployFiletr filter = new DeployFiletr();
            for (DeploymentDirectory deployment : deploymetDirss) {
                path = deployment.getPath();
                files = new File(path).listFiles(filter);
                fillFileList(files, list);
            }
        }

        return list;
    }

    /**
     * Lists all data source {@link File}s
     *
     * @return {@link List}<File>
     */
    public static List<File> listDataSources() {

        List<File> list = new ArrayList<File>();

        Collection<Configuration> configs = MetaContainer.CONFIGS.values();
        Set<String> paths = new HashSet<String>();
        Set<String> pathsCurrent;
        for (Configuration config : configs) {
            pathsCurrent = config.getDataSourcePath();
            if (CollectionUtils.valid(pathsCurrent)) {
                paths.addAll(pathsCurrent);
            }
        }

        File file;
        if (CollectionUtils.valid(paths)) {
            for (String path : paths) {
                file = new File(path);
                list.add(file);
            }
        }

        return list;
    }

    /**
     * Deploys application or data source file by passed file name
     *
     * @param fileName
     * @throws IOException
     */
    public static void deployFile(String fileName) throws IOException {

        WatchFileType type = checkType(fileName);
        if (type.equals(WatchFileType.DATA_SOURCE)) {
            FileParsers.parseDataSources(fileName);
        } else if (type.equals(WatchFileType.DEPLOYMENT)) {
            URL url = getAppropriateURL(fileName);
            deployFile(url);
        }
    }

    /**
     * Deploys application or data source file by passed {@link URL} instance
     *
     * @param url
     * @throws IOException
     */
    public static void deployFile(URL url) throws IOException {
        URL[] archives = { url };
        MetaContainer.getCreator().scanForBeans(archives);
    }

    /**
     * Removes from deployments application or data source file by passed
     * {@link URL} instance
     *
     * @param url
     * @throws IOException
     */
    public static void undeployFile(URL url) throws IOException {

        boolean valid = MetaContainer.undeploy(url);
        if (valid && RestContainer.hasRest()) {
            RestProvider.reload();
        }
    }

    /**
     * Removes from deployments application or data source file by passed file
     * name
     *
     * @param fileName
     * @throws IOException
     */
    public static void undeployFile(String fileName) throws IOException {

        WatchFileType type = checkType(fileName);
        if (type.equals(WatchFileType.DATA_SOURCE)) {
            Initializer.undeploy(fileName);
        } else if (type.equals(WatchFileType.DEPLOYMENT)) {
            URL url = getAppropriateURL(fileName);
            undeployFile(url);
        }
    }

    /**
     * Removes from deployments and deploys again application or data source
     * file by passed file name
     *
     * @param fileName
     * @throws IOException
     */
    public static void redeployFile(String fileName) throws IOException {
        undeployFile(fileName);
        deployFile(fileName);
    }

    /**
     * Handles file change event
     *
     * @param dir
     * @param currentEvent
     * @throws IOException
     */
    private void handleEvent(Path dir, WatchEvent<Path> event) throws IOException {

        if (ObjectUtils.notNull(event)) {
            Path prePath = event.context();
            Path path = dir.resolve(prePath);
            String fileName = path.toString();
            int count = event.count();
            Kind<?> kind = event.kind();
            if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                LogUtils.info(LOG, MODIFY_MESSAGE, fileName, count);
                redeployFile(fileName);
            } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                LogUtils.info(LOG, DELETE_MESSAGE, fileName, count);
                undeployFile(fileName);
            } else if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                LogUtils.info(LOG, CREATE_MESSAGE, fileName, count);
                redeployFile(fileName);
            }
        }
    }

    private void handleEvent(WatchEvent<?> event, Path dir) throws InterruptedException, IOException {

        Thread.sleep(SLEEP_TIME);
        WatchEvent<Path> typedEvent = ObjectUtils.cast(event);
        handleEvent(dir, typedEvent);
    }

    private boolean handleEvent(WatchKey key, WatchEvent<?> event, Path dir) throws InterruptedException, IOException {

        boolean run = key.reset() && key.isValid();

        if (run) {
            handleEvent(event, dir);
        }

        return run;
    }

    private boolean validate(WatchEvent<?> event) {
        return (event.kind() != StandardWatchEventKinds.OVERFLOW);
    }

    private boolean watchService(WatchService watch) throws InterruptedException, IOException {

        boolean run = Boolean.FALSE;

        WatchKey key = watch.take();
        List<WatchEvent<?>> events = key.pollEvents();
        WatchEvent<?> currentEvent = null;
        int times = ZERO_WATCH_STATUS;
        Path dir = ObjectUtils.cast(key.watchable(), Path.class);
        for (WatchEvent<?> event : events) {
            if (validate(event)) {
                if (times == ZERO_WATCH_STATUS || event.count() > currentEvent.count()) {
                    currentEvent = event;
                }

                times++;
                run = handleEvent(key, currentEvent, dir);
            }
        }

        return run;
    }

    /**
     * Runs file watch service
     *
     * @param watch
     * @throws IOException
     */
    private void runService(WatchService watch) throws IOException {

        boolean run = Boolean.TRUE;
        while (run) {
            try {
                run = watchService(watch);
            } catch (InterruptedException ex) {
                throw new IOException(ex);
            }
        }
    }

    /**
     * Registers path to watch service
     *
     * @param fs
     * @param path
     * @param watch
     * @throws IOException
     */
    private void registerPath(FileSystem fs, String path, WatchService watch) throws IOException {

        Path deployPath = fs.getPath(path);
        deployPath.register(watch, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.OVERFLOW, StandardWatchEventKinds.ENTRY_DELETE);
        runService(watch);
    }

    /**
     * Registers passed {@link File} array to watch service
     *
     * @param files
     * @param fs
     * @param watch
     * @throws IOException
     */
    private void registerPaths(File[] files, FileSystem fs, WatchService watch) throws IOException {

        String path;
        for (File file : files) {
            path = file.getPath();
            registerPath(fs, path, watch);
        }
    }

    /**
     * Registers deployments directories to watch service
     *
     * @param deploymentDirss
     * @param fs
     * @param watch
     * @throws IOException
     */
    private void registerPaths(Collection<DeploymentDirectory> deploymentDirss, FileSystem fs, WatchService watch)
            throws IOException {

        String path;
        boolean scan;
        File directory;
        File[] files;
        for (DeploymentDirectory deployment : deploymentDirss) {
            path = deployment.getPath();
            scan = deployment.isScan();
            if (scan) {
                directory = new File(path);
                files = directory.listFiles();

                if (CollectionUtils.valid(files)) {
                    registerPaths(files, fs, watch);
                }
            } else {
                registerPath(fs, path, watch);
            }
        }
    }

    /**
     * Registers data source path to watch service
     *
     * @param paths
     * @param fs
     * @param watch
     * @throws IOException
     */
    private void registerDsPaths(Collection<String> paths, FileSystem fs, WatchService watch) throws IOException {

        for (String path : paths) {
            registerPath(fs, path, watch);
        }
    }

    @Override
    public void run() {

        try {
            FileSystem fs = FileSystems.getDefault();
            WatchService watch = null;

            try {
                watch = fs.newWatchService();
            } catch (IOException ex) {
                LOG.error(ex.getMessage(), ex);
                throw ex;
            }

            if (CollectionUtils.valid(deployments)) {
                registerPaths(deployments, fs, watch);
            }

            if (CollectionUtils.valid(dataSources)) {
                registerDsPaths(dataSources, fs, watch);
            }
        } catch (IOException ex) {
            LOG.fatal(ex.getMessage(), ex);
            LOG.fatal("system going to shut down cause of hot deployment");
            try {
                ConnectionContainer.closeConnections();
            } catch (IOException iex) {
                LOG.fatal(iex.getMessage(), iex);
            }
            System.exit(ERROR_EXIT);
        } finally {
            DEPLOY_POOL.shutdown();
        }
    }

    /**
     * Starts watch service for application and data source files
     */
    public static void startWatch() {
        Watcher watcher = new Watcher();
        DEPLOY_POOL.submit(watcher);
    }
}
