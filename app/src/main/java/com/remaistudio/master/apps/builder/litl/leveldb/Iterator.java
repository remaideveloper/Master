package com.remaistudio.master.apps.builder.litl.leveldb;

public class Iterator extends NativeObject
{
    Iterator(final long n) {
        super(n);
    }
    
    private static native void nativeDestroy(final long p0);
    
    private static native byte[] nativeKey(final long p0);
    
    private static native void nativeNext(final long p0);
    
    private static native void nativePrev(final long p0);
    
    private static native void nativeSeek(final long p0, final byte[] p1);
    
    private static native void nativeSeekToFirst(final long p0);
    
    private static native void nativeSeekToLast(final long p0);
    
    private static native boolean nativeValid(final long p0);
    
    private static native byte[] nativeValue(final long p0);
    
    @Override
    protected void closeNativeObject(final long n) {
        nativeDestroy(n);
    }
    
    public byte[] getKey() {
        this.assertOpen("Iterator is closed");
        return nativeKey(this.mPtr);
    }
    
    public byte[] getValue() {
        this.assertOpen("Iterator is closed");
        return nativeValue(this.mPtr);
    }
    
    public boolean isValid() {
        this.assertOpen("Iterator is closed");
        return nativeValid(this.mPtr);
    }
    
    public void next() {
        this.assertOpen("Iterator is closed");
        nativeNext(this.mPtr);
    }
    
    public void prev() {
        this.assertOpen("Iterator is closed");
        nativePrev(this.mPtr);
    }
    
    public void seek(final byte[] array) {
        this.assertOpen("Iterator is closed");
        if (array == null) {
            throw new IllegalArgumentException();
        }
        nativeSeek(this.mPtr, array);
    }
    
    public void seekToFirst() {
        this.assertOpen("Iterator is closed");
        nativeSeekToFirst(this.mPtr);
    }
    
    public void seekToLast() {
        this.assertOpen("Iterator is closed");
        nativeSeekToLast(this.mPtr);
    }
}
