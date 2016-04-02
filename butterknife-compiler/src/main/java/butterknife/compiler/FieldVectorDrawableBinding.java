package butterknife.compiler;

import com.squareup.javapoet.ClassName;

final class FieldVectorDrawableBinding {
  private final int id;
  private final String name;
  private final ClassName className;
  private final boolean isCompat;
  private final boolean isAnimated;

  FieldVectorDrawableBinding(int id, String name, String typeString, boolean isAnimated) {
    this.id = id;
    this.name = name;
    this.className = ClassName.bestGuess(typeString);
    this.isCompat = typeString.contains("VectorDrawableCompat");
    this.isAnimated = isAnimated;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public ClassName getClassName() {
    return className;
  }

  public boolean isCompat() {
    return isCompat;
  }

  public boolean isAnimated() {
    return isAnimated;
  }
}
