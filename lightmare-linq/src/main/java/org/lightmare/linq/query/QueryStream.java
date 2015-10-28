package org.lightmare.linq.query;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.lightmare.linq.tuples.ParameterTuple;

public class QueryStream<T> {

	private QueryStreamSub<T> stream;

	private QueryStream() {
	}

	public <F> QueryStream<T> eq(FieldCaller<F> field, F value) throws IOException {
		stream.eq(field, value);
		return this;
	}

	public <F> QueryStream<T> more(FieldCaller<F> field, F value) throws IOException {
		stream.more(field, value);
		return this;
	}

	public <F> QueryStream<T> less(FieldCaller<F> field, F value) throws IOException {
		stream.less(field, value);
		return this;
	}

	public <F> QueryStream<T> moreOrEq(FieldCaller<F> field, F value) throws IOException {
		stream.moreOrEq(field, value);
		return this;
	}

	public <F> QueryStream<T> lessOrEq(FieldCaller<F> field, F value) throws IOException {
		stream.lessOrEq(field, value);
		return this;
	}

	public <F> QueryStream<T> contains(FieldCaller<F> field, F value) throws IOException {
		stream.contains(field, value);
		return this;
	}

	public QueryStream<T> like(FieldCaller<String> field, String value) throws IOException {
		stream.startsWith(field, value);
		return this;
	}

	public QueryStream<T> startsWith(FieldCaller<String> field, String value) throws IOException {
		stream.startsWith(field, value);
		return this;
	}

	public QueryStream<T> endsWith(FieldCaller<String> field, String value) throws IOException {
		stream.endsWith(field, value);
		return this;
	}

	public QueryStream<T> contains(FieldCaller<String> field, String value) throws IOException {
		stream.endsWith(field, value);
		return this;
	}

	public static <T> QueryStream<T> select(final EntityManager em, final Class<T> type) {

		QueryStream<T> wrapper = new QueryStream<>();

		QueryStreamSub<T> stream = QueryStreamSub.select(em, type);
		wrapper.stream = stream;

		return wrapper;
	}

	public QueryStream<T> where() {
		stream.where();
		return this;
	}

	public QueryStream<T> and() {
		stream.and();
		return this;
	}

	public QueryStream<T> or() {
		stream.or();
		return this;
	}

	public String sql() {
		return stream.sql();
	}

	public Set<ParameterTuple<?>> getParameters() {
		return stream.getParameters();
	}

	public List<T> toList() {
		return stream.toList();
	}

	public T get() {
		return stream.get();
	}

	public void setWerbose(boolean werbose) {
		this.stream.setWerbose(werbose);
	}

	@Override
	public String toString() {
		return stream.sql();
	}
}
