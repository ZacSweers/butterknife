package butterknife;

import android.support.annotation.DrawableRes;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Bind a field to the specified drawable resource ID. Note that this works on both framework and
 * support VectorDrawables. If the target is a framework version, it will just cast the retrieved
 * drawable. If it is the compat version, it will use the support library's
 * {@code VectorDrawableCompat.create(Context,int,Theme)} implementation.
 *
 * <pre><code>
 * {@literal @}BindVD(R.drawable.placeholder)
 * VectorDrawable placeholder;
 * {@literal @}BindVD(value = R.drawable.placeholder)
 * VectorDrawableCompat tintedPlaceholder;
 * </code></pre>
 */
@Retention(CLASS) @Target(FIELD)
public @interface BindVD {
  /** Drawable resource ID to which the field will be bound. */
  @DrawableRes int value();
}
