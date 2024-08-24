package japicmp.util;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.Modifier;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CtClassBuilder {
	public static final String DEFAULT_CLASS_NAME = "japicmp.Test";
	private String name = DEFAULT_CLASS_NAME;
	private int modifier = Modifier.PUBLIC;
	private final Map<String, CtElement[]> annotations = new HashMap<>();
	private Optional<CtClass> superclass = Optional.empty();
	private final List<CtClass> interfaces = new ArrayList<>();

	public CtClassBuilder name(String name) {
		this.name = name;
		return this;
	}

	public CtClassBuilder syntheticModifier() {
		this.modifier = this.modifier | ModifierHelper.ACC_SYNTHETIC;
		return this;
	}

	public CtClassBuilder withAnnotation(String annotation, CtElement... elements) {
		this.annotations.put(annotation, elements);
		return this;
	}

	public CtClassBuilder abstractModifier() {
		this.modifier = this.modifier | Modifier.ABSTRACT;
		return this;
	}

	public CtClassBuilder finalModifier() {
		this.modifier = this.modifier | Modifier.FINAL;
		return this;
	}

	// class cannot be private or (package) protected (see https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.1)
	public CtClassBuilder notPublicModifier() {
		this.modifier = this.modifier & ~Modifier.PUBLIC;
		return this;
	}

	public CtClassBuilder enumModifier() {
		this.modifier = this.modifier | Modifier.ENUM;
		return this;
	}

	public CtClassBuilder withSuperclass(CtClass superclass) {
		this.superclass = Optional.of(superclass);
		return this;
	}

	public CtClass addToClassPool(ClassPool classPool) {
		CtClass ctClass;
		if (this.superclass.isPresent()) {
			ctClass = classPool.makeClass(this.name, this.superclass.get());
		} else {
			ctClass = classPool.makeClass(this.name);
		}
		ctClass.setModifiers(this.modifier);
		for (String annotation : annotations.keySet()) {
			ClassFile classFile = ctClass.getClassFile();
			ConstPool constPool = classFile.getConstPool();
			AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
			Annotation annot = new Annotation(annotation, constPool);
			for (CtElement element : annotations.get(annotation)) {
				annot.addMemberValue(element.name, element.value.apply(constPool));
			}
			attr.setAnnotation(annot);
			ctClass.getClassFile2().addAttribute(attr);
		}
		for (CtClass interfaceCtClass : interfaces) {
			ctClass.addInterface(interfaceCtClass);
		}
		return ctClass;
	}

	public static CtClassBuilder create() {
		return new CtClassBuilder();
	}

	public CtClassBuilder implementsInterface(CtClass ctClass) {
		interfaces.add(ctClass);
		return this;
	}
}
