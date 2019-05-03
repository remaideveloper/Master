package com.remaistudio.master.apps.builder.litl.leveldb;

import java.io.File;
import java.nio.ByteBuffer;

public class DB extends NativeObject
{
    private boolean mDestroyOnClose;
    private final File mPath;
    
    public DB(final File mPath) {
        this.mDestroyOnClose = false;
        System.loadLibrary("leveldbjni");
        if (mPath == null) {
            throw new NullPointerException();
        }
        this.mPath = mPath;
    }
    
    public static void destroy(final File file) {
        nativeDestroy(file.getAbsolutePath());
    }
    
    private static native void nativeClose(final long p0);
    
    private static native void nativeDelete(final long p0, final byte[] p1);
    
    private static native void nativeDestroy(final String p0);
    
    private static native byte[] nativeGet(final long p0, final long p1, final ByteBuffer p2);
    
    private static native byte[] nativeGet(final long p0, final long p1, final byte[] p2);
    
    private static native long nativeGetSnapshot(final long p0);
    
    private static native long nativeIterator(final long p0, final long p1);
    
    private static native long nativeOpen(final String p0);
    
    private static native void nativePut(final long p0, final byte[] p1, final byte[] p2);
    
    private static native void nativeReleaseSnapshot(final long p0, final long p1);
    
    private static native void nativeWrite(final long p0, final long p1);
    
    public static native String stringFromJNI();
    
    @Override
    protected void closeNativeObject(final long n) {
        nativeClose(n);
        if (this.mDestroyOnClose) {
            destroy(this.mPath);
        }
    }
    
    public void delete(final byte[] array) {
        this.assertOpen("Database is closed");
        if (array == null) {
            throw new NullPointerException();
        }
        nativeDelete(this.mPtr, array);
    }
    
    public void destroy() {
        this.mDestroyOnClose = true;
        if (this.getPtr() == 0L) {
            destroy(this.mPath);
        }
    }
    
    public byte[] get(final Snapshot snapshot, final ByteBuffer byteBuffer) {
        this.assertOpen("Database is closed");
        if (byteBuffer == null) {
            throw new NullPointerException();
        }
        final long mPtr = this.mPtr;
        long ptr;
        if (snapshot != null) {
            ptr = snapshot.getPtr();
        }
        else {
            ptr = 0L;
        }
        return nativeGet(mPtr, ptr, byteBuffer);
    }
    
    public byte[] get(final Snapshot snapshot, final byte[] array) {
        this.assertOpen("Database is closed");
        if (array == null) {
            throw new NullPointerException();
        }
        final long mPtr = this.mPtr;
        long ptr;
        if (snapshot != null) {
            ptr = snapshot.getPtr();
        }
        else {
            ptr = 0L;
        }
        return nativeGet(mPtr, ptr, array);
    }
    
    public byte[] get(final ByteBuffer byteBuffer) {
        return this.get(null, byteBuffer);
    }
    
    public byte[] get(final byte[] array) {
        return this.get(null, array);
    }
    
    public Snapshot getSnapshot() {
        this.assertOpen("Database is closed");
        this.ref();
        return new Snapshot(nativeGetSnapshot(this.mPtr)) {
            @Override
            protected void closeNativeObject(final long n) {
                nativeReleaseSnapshot(DB.this.getPtr(), this.getPtr());
                DB.this.unref();
            }
        };
    }
    
    public Iterator iterator() {
        return this.iterator(null);
    }
    
    public Iterator iterator(final Snapshot snapshot) {
        this.assertOpen("Database is closed");
        this.ref();
        if (snapshot != null) {
            snapshot.ref();
        }
        final long mPtr = this.mPtr;
        long ptr;
        if (snapshot != null) {
            ptr = snapshot.getPtr();
        }
        else {
            ptr = 0L;
        }
        return new Iterator(nativeIterator(mPtr, ptr)) {
            @Override
            protected void closeNativeObject(final long n) {
                super.closeNativeObject(n);
                if (snapshot != null) {
                    snapshot.unref();
                }
                DB.this.unref();
            }
        };
    }
    
    public void open() {
        this.mPtr = nativeOpen(this.mPath.getAbsolutePath());
    }
    
    public void put(final byte[] array, final byte[] array2) {
        this.assertOpen("Database is closed");
        if (array == null) {
            throw new NullPointerException("key");
        }
        if (array2 == null) {
            throw new NullPointerException("value");
        }
        nativePut(this.mPtr, array, array2);
    }
    
    public void write(final WriteBatch writeBatch) {
        this.assertOpen("Database is closed");
        if (writeBatch == null) {
            throw new NullPointerException();
        }
        nativeWrite(this.mPtr, writeBatch.getPtr());
    }
    
    public abstract static class Snapshot extends NativeObject
    {
        Snapshot(final long n) {
            super(n);
        }
    }
}
