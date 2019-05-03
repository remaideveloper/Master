package com.remaistudio.master.apps.builder.litl.leveldb;

import android.text.TextUtils;
import android.util.Log;

import java.io.Closeable;
import java.io.Serializable;

abstract class NativeObject implements Closeable
{
    private static final String TAG;
    protected long mPtr;
    private int mRefCount;
    
    static {
        TAG = NativeObject.class.getSimpleName();
    }
    
    protected NativeObject() {
        this.mRefCount = 0;
        this.ref();
    }
    
    protected NativeObject(final long mPtr) {
        this();
        if (mPtr == 0L) {
            throw new OutOfMemoryError("Failed to allocate native object");
        }
        this.mPtr = mPtr;
    }
    
    protected void assertOpen(final String s) {
        if (this.getPtr() == 0L) {
            throw new IllegalStateException(s);
        }
    }
    
    @Override
    public void close() {
        synchronized (this) {
            if (this.mPtr != 0L) {
                this.unref();
            }
        }
    }
    
    protected abstract void closeNativeObject(final long p0);
    
    @Override
    protected void finalize() throws Throwable {
        if (this.mPtr != 0L) {
            Serializable s;
            String s2;
            for (s = this.getClass(), s2 = ((Class)s).getSimpleName(); TextUtils.isEmpty(s2); s2 = ((Class)s).getSimpleName()) {
                s = ((Class<? extends NativeObject>)s).getSuperclass();
            }
            Log.w(NativeObject.TAG, "NativeObject " + s2 + " refcount: " + this.mRefCount + " id: " + System.identityHashCode(this) + " was finalized before native resource was closed, did you forget to call close()?");
        }
        super.finalize();
    }
    
    protected long getPtr() {
        synchronized (this) {
            return this.mPtr;
        }
    }
    
    void ref() {
        synchronized (this) {
            ++this.mRefCount;
        }
    }
    
    void unref() {
        synchronized (this) {
            if (this.mRefCount <= 0) {
                throw new IllegalStateException("Reference count is already 0");
            }
        }
        --this.mRefCount;
        if (this.mRefCount == 0) {
            this.closeNativeObject(this.mPtr);
            this.mPtr = 0L;
        }
    }
    // monitorexit(this)
}
