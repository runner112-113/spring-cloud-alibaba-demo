package com.runner.processor;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * My {@link AbstractProcessor}
 *
 * @author Runner
 * @version 1.0
 * @since 2024/10/30 17:47
 */

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"com.runner.processor.MyAnnotation"})
public class MyAnnotationProcessor extends AbstractProcessor {


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

//        annotations.forEach(item ->　);
        if (roundEnv.processingOver()) {
            // write out
        }
        return true;
    }
}
