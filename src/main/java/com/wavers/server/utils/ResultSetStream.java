package com.wavers.server.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ResultSetStream {

    public static Stream<ResultSet> toStream(ResultSet resultSet) {
        // Create a Spliterator from the ResultSet
        ResultSetSpliterator spliterator = new ResultSetSpliterator(resultSet);

        // Create a Stream from the Spliterator
        return StreamSupport.stream(spliterator, false);
    }

    // Creates iterator from Result Set
    private static class ResultSetSpliterator extends Spliterators.AbstractSpliterator<ResultSet> {
        private final ResultSet resultSet;

        ResultSetSpliterator(ResultSet resultSet) {
            super(Long.MAX_VALUE, Spliterator.IMMUTABLE | Spliterator.NONNULL);
            this.resultSet = resultSet;
        }

        @Override
        public boolean tryAdvance(java.util.function.Consumer<? super ResultSet> action) {
            try {
                if (resultSet.next()) {
                    action.accept(resultSet);
                    return true;
                } else {
                    return false;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}