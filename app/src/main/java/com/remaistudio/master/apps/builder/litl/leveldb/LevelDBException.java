package com.remaistudio.master.apps.builder.litl.leveldb;

public class LevelDBException extends RuntimeException
{
    private static final long serialVersionUID = 2903013251786326801L;
    
    public LevelDBException() {
    }
    
    public LevelDBException(final String s) {
        super(s);
    }
    
    public LevelDBException(final String s, final Throwable t) {
        super(s, t);
    }
}
