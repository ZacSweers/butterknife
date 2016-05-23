package butterknife;


import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import butterknife.compiler.ButterKnifeProcessor;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class VectorDrawableTest {

  @Test public void vector() {
    JavaFileObject source = JavaFileObjects.forSourceString("test.Test", ""
        + "package test;\n"
        + "\n"
        + "import android.graphics.drawable.AnimatedVectorDrawable;\n"
        + "import android.graphics.drawable.VectorDrawable;\n"
        + "import android.support.graphics.drawable.AnimatedVectorDrawableCompat;\n"
        + "import android.support.graphics.drawable.VectorDrawableCompat;\n"
        + "import android.view.View;\n"
        + "\n"
        + "import butterknife.BindAVD;\n"
        + "import butterknife.BindVD;\n"
        + "import butterknife.ButterKnife;\n"
        + "\n"
        + "public final class Test {\n"
        + "\n"
        + "  @BindVD(1)\n"
        + "  VectorDrawable backVector;\n"
        + "\n"
        + "  @BindAVD(2)\n"
        + "  AnimatedVectorDrawable avdLikes;\n"
        + "\n"
        + "  @BindVD(3)\n"
        + "  VectorDrawableCompat backVectorCompat;\n"
        + "\n"
        + "  @BindAVD(4)\n"
        + "  AnimatedVectorDrawableCompat avdLikesCompat;\n"
        + "\n"
        + "  public Test(View view) {\n"
        + "    ButterKnife.bind(this, view);\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/Test$$ViewBinder", ""
        + "// Generated code from Butter Knife. Do not modify!\n"
        + "package test;\n"
        + "\n"
        + "import android.content.Context;\n"
        + "import android.content.res.Resources;\n"
        + "import android.graphics.drawable.AnimatedVectorDrawable;\n"
        + "import android.graphics.drawable.VectorDrawable;\n"
        + "import android.support.graphics.drawable.AnimatedVectorDrawableCompat;\n"
        + "import android.support.graphics.drawable.VectorDrawableCompat;\n"
        + "import butterknife.Unbinder;\n"
        + "import butterknife.internal.Finder;\n"
        + "import butterknife.internal.Utils;\n"
        + "import butterknife.internal.ViewBinder;\n"
        + "import java.lang.Object;\n"
        + "import java.lang.Override;\n"
        + "import java.lang.SuppressWarnings;\n"
        + "\n"
        + "public class Test$$ViewBinder<T extends Test> implements ViewBinder<T> {\n"
        + "  @Override\n"
        + "  @SuppressWarnings(\"ResourceType\")\n"
        + "  public Unbinder bind(final Finder finder, final T target, Object source) {\n"
        + "    Context context = finder.getContext(source);\n"
        + "    Resources res = context.getResources();\n"
        + "    Resources.Theme theme = context.getTheme();\n"
        + "    target.backVector = (VectorDrawable) Utils.getDrawable(res, theme, 1);\n"
        + "    target.backVectorCompat = VectorDrawableCompat.create(res, 2, theme);\n"
        + "    target.avdLikes = (AnimatedVectorDrawable) Utils.getDrawable(res, theme, 3);\n"
        + "    target.avdLikesCompat = AnimatedVectorDrawableCompat.create(context, 4);\n"
        + "    return Unbinder.EMPTY;\n"
        + "  }\n"
        + "}\n");

    assertAbout(javaSource()).that(source)
        .processedWith(new ButterKnifeProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(expectedSource);
  }

}
