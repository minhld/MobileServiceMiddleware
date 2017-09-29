package com.usu.servicemiddleware.annotations;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * Created by minhld on 9/29/2017.
 */
public class CompileTest {
    public static void compile(String javaFullPackage, String className, String javaFileContents) throws Exception {
        try {
            File helloWorldJava = new File(javaFullPackage);


            // check the availability of the parent path
            if (!helloWorldJava.getParentFile().exists()) {
                helloWorldJava.getParentFile().mkdirs();
            }

            // write to a physical path
            Writer writer = new FileWriter(helloWorldJava);
            writer.write(javaFileContents);
            writer.flush();
            writer.close();

            // compilation Requirements
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

            // This sets up the class path that the compiler will use.
            // I've added the .jar file that contains the DoStuff interface within in it...
            List<String> optionList = new ArrayList<>();

            Iterable<? extends JavaFileObject> compilationUnit = fileManager.
                                        getJavaFileObjectsFromFiles(Arrays.asList(helloWorldJava));
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics,
                                        optionList, null, compilationUnit);

            // Compilation Requirements
            if (task.call()) {
                /** Load and execute *************************************************************************************************/
                System.out.println("Yipe");
                // Create a new custom class loader, pointing to the directory that contains the compiled
                // classes, this should point to the top of the package structure!
                URLClassLoader classLoader = new URLClassLoader(new URL[]{ new File("./").toURI().toURL() });

                // Load the class from the classloader by name....
                Class<?> loadedClass = classLoader.loadClass(className);

                // Create a new instance...
                Object obj = loadedClass.newInstance();

                // Santity check
                if (obj instanceof DoStuff) {
                    // Cast to the DoStuff interface
                    DoStuff stuffToDo = (DoStuff)obj;
                    // Run it baby
                    stuffToDo.doStuff();
                }
                /************************************************************************************************* Load and execute **/
            } else {
                for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                    System.out.format("Error on line %d in %s%n",
                            diagnostic.getLineNumber(),
                            diagnostic.getSource().toUri());
                }
            }
            fileManager.close();
        } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException exp) {
            exp.printStackTrace();
        }
    }
}
