package com.usu.servicemiddleware.processors;

import com.google.auto.service.AutoService;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;

import com.google.common.collect.ImmutableList;
import com.usu.servicemiddleware.annotations.MobileService;
import com.usu.servicemiddleware.annotations.ServiceMethod;

/**
 * Created by lee on 9/20/17.
 */
@AutoService(Processor.class)
@SuppressWarnings("unused")
public class MobileServiceProcessor extends AbstractProcessor {
    private Messager messager = null;
//    private Filer filer = null;

    //The processor has to have an empty constructor
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment){
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager();
//        filer = processingEnvironment.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        //get all elements annotated with StatusInfo
        // Collection<? extends Element> annotatedElements = env.getElementsAnnotatedWith(MobileService.class);
        Collection<? extends Element> annotatedElements = env.getElementsAnnotatedWith(MobileService.class);

        //filter out elements we don't need
        List<TypeElement> types = new ImmutableList.Builder<TypeElement>().addAll(
                ElementFilter.typesIn(annotatedElements)).build();

        for (TypeElement type : types) {
            //interfaces are types too, but we only need classes
            //we need to check if the TypeElement is a valid class
            if (isValidClass(type, MobileService.class.getName())) {
                // writeSourceFile(type);
                // MobileServiceCreator.generateServer(processingEnv, type);
                // MobileServiceCreator.generateClient(processingEnv, type);

                MobileServiceBinCreator.generateServer(processingEnv, type);
                MobileServiceBinCreator.generateClient(processingEnv, type);
            } else {
                return true;
            }
        }
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new HashSet<>();
        annotations.add(MobileService.class.getCanonicalName());
        annotations.add(ServiceMethod.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private boolean isValidClass(TypeElement type, String className){
        if(type.getKind() != ElementKind.CLASS){
            messager.printMessage(Diagnostic.Kind.ERROR, type.getSimpleName() +
                    " only classes can be annotated with " + className);
            return false;
        }

        if(type.getModifiers().contains(Modifier.PRIVATE)){
            messager.printMessage(Diagnostic.Kind.ERROR, type.getSimpleName() +
                    " only public classes can be annotated with " + className);
            return false;
        }

        if(type.getModifiers().contains(Modifier.ABSTRACT)){
            messager.printMessage(Diagnostic.Kind.ERROR, type.getSimpleName() +
                    " only non abstract classes can be annotated with " + className);
            return false;
        }

        return true;
    }


}
