package ge.gov.mia.lightmare.jndi;

import java.util.Hashtable;

import javax.naming.NamingException;

import org.osjava.sj.memory.MemoryContext;

public class DSContext extends MemoryContext {

    public DSContext(Hashtable<?, ?> env) {
        super(env);
    }

    @Override
    public void close() throws NamingException {
        // super.close();
    }
}
