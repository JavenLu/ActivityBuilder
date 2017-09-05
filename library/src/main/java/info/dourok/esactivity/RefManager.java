package info.dourok.esactivity;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tiaolins on 2017/8/31.
 */

public class RefManager {
  public final static String KEY_OF_MAP = "info.dourok.esactivity#KeyOfMap(DON_T_MODIFY)";
  private SparseArray<Map<String, Object>> sGlobalRefMap = new SparseArray<>();
  private static RefManager sInstance = new RefManager();

  public static RefManager getInstance() {
    return sInstance;
  }

  public <T> T get(Activity context, String key) {
    return get(context.getIntent(), key);
  }

  //FIXME 考虑装箱类型 null 拆箱报错的情况
  public <T> T get(Intent intent, String key) {
    Map<String, Object> refMap = getRefMap(intent);
    if (refMap != null) {
      return (T) refMap.get(key);
    } else {
      return null;
    }
  }

  public <T> void put(BaseBuilder builder, String key, T value) {
    Map<String, Object> refMap = getRefMap(builder);
    refMap.put(key, value);
  }

  /**
   * 用于 setResult 的 Intent
   * @param intent
   * @param key
   * @param value
   * @param <T>
   */
  public <T> void put(Intent intent, String key, T value) {
    Map<String, Object> refMap = createRefMap(intent);
    refMap.put(key, value);
  }

  /**
   * 每个 buidler 实例都有独立的 ReFMap
   */
  public @NonNull Map<String, Object> getRefMap(BaseBuilder builder) {
    int keyOfMap = builder.hashCode();
    Map<String, Object> refMap = sGlobalRefMap.get(keyOfMap);
    if (refMap == null) {
      refMap = new HashMap<>();
      sGlobalRefMap.put(keyOfMap, refMap);
      Intent intent = builder.asIntent().getIntent();
      intent.putExtra(KEY_OF_MAP, keyOfMap);
    }
    return refMap;
  }

  /**
   * 用于 setResult 的 Intent
   */
  public @NonNull Map<String, Object> createRefMap(Intent intent) {
    int keyOfMap = intent.hashCode();
    Map<String, Object> refMap = sGlobalRefMap.get(keyOfMap);
    if (refMap == null) {
      refMap = new HashMap<>();
      sGlobalRefMap.put(keyOfMap, refMap);
      intent.putExtra(KEY_OF_MAP, keyOfMap);
    }
    return refMap;
  }

  public @Nullable Map<String, Object> getRefMap(Activity context) {
    return getRefMap(context.getIntent());
  }

  public @Nullable Map<String, Object> getRefMap(Intent intent) {
    if (intent.hasExtra(KEY_OF_MAP)) {
      int keyOfMap = intent.getIntExtra(KEY_OF_MAP, 0);
      return sGlobalRefMap.get(keyOfMap);
    } else {
      return null;
    }
  }

  public void clearRefs(Activity context) {
    Intent intent = context.getIntent();
    if (intent.hasExtra(KEY_OF_MAP)) {
      int keyOfMap = intent.getIntExtra(KEY_OF_MAP, 0);
      sGlobalRefMap.remove(keyOfMap);
    }
  }
}
