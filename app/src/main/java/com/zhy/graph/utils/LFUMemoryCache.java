package com.zhy.graph.utils;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * @author yuzhuo
 * 
 */
public class LFUMemoryCache {

	private static final String TAG = "MemoryCache";
	private Map<String, Bitmap> cache = Collections
			.synchronizedMap(new LinkedHashMap<String, Bitmap>(10, 1.5f, true));
	private long size = 0;
	private long limit = 1000000;
	// 命中次数
	private static int fitcount = 0;
	// 请求缓存次数
	private static int putcount = 0;

	public LFUMemoryCache(long size) {
		setLimit(size);
	}

	public void setLimit(long new_limit) {
		limit = new_limit;
		Log.i(TAG, "MemoryCache的限制是" + limit / 1024. / 1024. + "MB");
	}

	public Bitmap get(String id) {
		try {
			if (!cache.containsKey(id))
				return null;
			return cache.get(id);
		} catch (NullPointerException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public double hitrate() {

		return (double) fitcount / putcount;
	}

	public void setWindow(Bitmap bitmap, int time) {

		if (time < cache.size() - 1) {

			Iterator<Entry<String, Bitmap>> iter = cache.entrySet().iterator();
			while (iter.hasNext()) {

			}
		}

	}

	public void put(String id, Bitmap bitmap) {
		try {
			putcount++;
			if (cache.containsKey(id)) {
				fitcount++;

			} else {
				cache.put(id, bitmap);
				size += getSizeInBytes(bitmap);
				checkSize();
			}

		} catch (Throwable th) {
			th.printStackTrace();
		}
	}

	public boolean remove(String id, Bitmap bitmap) {
		try {
			if (cache.containsKey(id)) {
				cache.remove(id);
				size -= getSizeInBytes(cache.get(id));
				return true;
			}
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	private void checkSize() {
		Log.i(TAG, "cache size=" + size + " length=" + cache.size());
		if (size > limit) {
			Iterator<Entry<String, Bitmap>> iter = cache.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, Bitmap> entry = iter.next();
				size -= getSizeInBytes(entry.getValue());
				iter.remove();
				if (size <= limit)
					break;
			}
			Log.i(TAG, "新大小" + cache.size());
		}
	}

	public void clear() {
		try {
			cache.clear();
			size = 0;
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}
	}

	long getSizeInBytes(Bitmap bitmap) {
		if (bitmap == null)
			return 0;
		return bitmap.getRowBytes() * bitmap.getHeight();
	}
}