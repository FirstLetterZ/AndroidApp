package com.zpf.processor;

import com.google.auto.service.AutoService;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;


@AutoService(Processor.class)
public class AnnotationProcessor extends AbstractProcessor {

    private Types mTypes;
    private Elements mElements;
    private Filer mFiler;
    private Messager mMessager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mTypes = processingEnv.getTypeUtils();
        mElements = processingEnv.getElementUtils();
        mFiler = processingEnv.getFiler();
        mMessager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, "============================ZPF process begin=============================");
        mMessager.printMessage(Diagnostic.Kind.NOTE, "roundEnv=" + roundEnv.toString());
        mMessager.printMessage(Diagnostic.Kind.NOTE, "annotations=" + annotations.toString());
//        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Destination.class);
//        if (elements != null && elements.size() > 0) {
//            TypeSpec.Builder typeBuilder = TypeSpec.classBuilder("Portal");
//            String packageName = null;
//            for (Element element : elements) {
//                String qualifiedName = ((TypeElement) element).getQualifiedName().toString();
//                mMessager.printMessage(Diagnostic.Kind.NOTE, "QualifiedName=" + qualifiedName);
//                String name = qualifiedName.replace(".", "_").toUpperCase();
//                FieldSpec fieldSpec = FieldSpec.builder(String.class, name, Modifier.PUBLIC, Modifier.STATIC)
//                        .initializer("$S", qualifiedName)
//                        .build();
//                typeBuilder.addField(fieldSpec);
//                if (packageName == null) {
//                    packageName = qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
//                }
//            }
//            if(packageName==null){
//                packageName="com.zpf";
//            }
//            MethodSpec main = MethodSpec.methodBuilder("main")
//                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
//                    .addParameter(String[].class, "args")
//                    .addStatement("$T.out.println($S)", System.class, "Hello World")
//                    .build();
//            TypeSpec typeSpec
//                    = typeBuilder
//                    .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
//                    .addMethod(main)
//                    .build();
//            JavaFile javaFile = JavaFile.builder(packageName, typeSpec)
//                    .build();
//            try {
//                javaFile.writeTo(mFiler);
//            } catch (Exception e) {
//                mMessager.printMessage(Diagnostic.Kind.NOTE, "Generate file failed:" + e.getMessage());
//            }
//        }
        mMessager.printMessage(Diagnostic.Kind.NOTE, "============================ZPF process end=============================");
        return true;
    }

//    @Override
//    public Set<String> getSupportedAnnotationTypes() {
//        return ImmutableSet.of(Destination.class.getCanonicalName());
//    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

}
