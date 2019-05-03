package com.remaistudio.master.apps.builder.litl.leveldb;

import java.nio.ByteBuffer;

public class WriteBatch extends NativeObject
{
    public WriteBatch() {
        super(nativeCreate());
    }
    
    private static native void nativeClear(final long p0);
    
    private static native long nativeCreate();
    
    private static native void nativeDelete(final long p0, final ByteBuffer p1);
    
    private static native void nativeDestroy(final long p0);
    
    private static native void nativePut(final long p0, final ByteBuffer p1, final ByteBuffer p2);
    
    public void clear() {
        this.assertOpen("WriteBatch is closed");
        nativeClear(this.mPtr);
    }
    
    @Override
    protected void closeNativeObject(final long n) {
        nativeDestroy(n);
    }
    
    public void delete(final ByteBuffer byteBuffer) {
        this.assertOpen("WriteBatch is closed");
        if (byteBuffer == null) {
            throw new NullPointerException("key");
        }
        nativeDelete(this.mPtr, byteBuffer);
    }
    
    public void put(final ByteBuffer byteBuffer, final ByteBuffer byteBuffer2) {
        this.assertOpen("WriteBatch is closed");
        if (byteBuffer == null) {
            throw new NullPointerException("key");
        }
        if (byteBuffer2 == null) {
            throw new NullPointerException("value");
        }
        nativePut(this.mPtr, byteBuffer, byteBuffer2);
    }
}
