package org.proces;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toMap;
import static javax.lang.model.SourceVersion.RELEASE_21;
import static javax.lang.model.element.ElementKind.FIELD;

/*
 A classe AbstractProcessor é uma classe abstrata que implementa a interface Processor,
 fornecendo uma implementação básica para processar anotações. Ela é parte do framework
 de processamento de anotações do Java e é usada para criar processadores de anotações
 personalizados.
 A anotação @SupportedAnnotationTypes é usada para especificar quais tipos de anotações
 o processador suporta.
 A anotação @SupportedSourceVersion é usada para especificar as versões que o processador
 suporta. Neste caso, apenas para fins didáticos estamos especificando que o processador
 suporta a versão 21 do Java.
 */

@SupportedAnnotationTypes("org.annot.Builder")
@SupportedSourceVersion(RELEASE_21)
//@AutoService(Processor.class)
public class BuilderProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        for(var annotation: annotations){
            for (var element: roundEnv.getElementsAnnotatedWith(annotation)){
                Map<String, TypeMirror> fields = element.getEnclosedElements().stream()
                        .filter(e -> e.getKind() == FIELD)
                        .collect(toMap(e -> e.getSimpleName().toString(), Element::asType));
                var packageName = processingEnv.getElementUtils().getPackageOf(element).toString();
                var className = element.getSimpleName().toString();
                var builderName = className + "Builder";

                var typeSpec = new BuilderGenerator().create(packageName, className, builderName, fields);
                var javaFile = JavaFile.builder(packageName, typeSpec)
                        .indent("    ")
                        .build();
                try(
                        var out = new PrintWriter(processingEnv.getFiler()
                                .createSourceFile(builderName)
                                .openWriter())
                ){
                    out.write(javaFile.toString());
                }catch (IOException ex){
                    throw new RuntimeException(ex);
                }
            }
        }
        return true;
    }
}
