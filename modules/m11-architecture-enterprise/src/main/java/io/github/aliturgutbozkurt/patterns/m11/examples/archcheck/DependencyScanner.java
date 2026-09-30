package io.github.aliturgutbozkurt.patterns.m11.examples.archcheck;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.classfile.ClassFile;
import java.lang.classfile.ClassModel;
import java.lang.classfile.FieldModel;
import java.lang.classfile.MethodModel;
import java.lang.classfile.constantpool.ClassEntry;
import java.lang.classfile.constantpool.PoolEntry;
import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * How architecture tools read code: not the source, the bytecode. Parses a class file with the Class-File API
 * (JEP 484) and collects every type it names — constant-pool class entries plus field and method descriptors.
 * <b>Limitation:</b> generic type arguments live only in the {@code Signature} attribute, which this scanner does not
 * read, so {@code Consumer<OrderEvent>} yields {@code Consumer} but not {@code OrderEvent}. ArchUnit reads them.
 *
 * @see "m11 lesson, section Architecture rules — how the tools work"
 */
public final class DependencyScanner {

    /** Fully qualified names of the types {@code type}'s class file refers to, sorted, without itself. */
    public SortedSet<String> dependenciesOf(Class<?> type) {
        ClassModel model = parse(type);
        SortedSet<String> found = new TreeSet<>();
        for (PoolEntry entry : model.constantPool()) {
            if (entry instanceof ClassEntry classEntry) {
                name(classEntry.asSymbol()).ifPresent(found::add);
            }
        }
        for (FieldModel field : model.fields()) {
            name(field.fieldTypeSymbol()).ifPresent(found::add); // types used only as a field type
        }
        for (MethodModel method : model.methods()) {
            MethodTypeDesc descriptor = method.methodTypeSymbol();
            name(descriptor.returnType()).ifPresent(found::add);
            descriptor.parameterList().forEach(parameter -> name(parameter).ifPresent(found::add));
        }
        found.remove(type.getName());
        return found;
    }

    /** The class file's major version (71 for Java 27). */
    public int majorVersion(Class<?> type) {
        return parse(type).majorVersion();
    }

    private static ClassModel parse(Class<?> type) {
        String fileName = type.getName().substring(type.getName().lastIndexOf('.') + 1) + ".class";
        try (InputStream in = type.getResourceAsStream(fileName)) {
            if (in == null) {
                throw new IllegalStateException("no class file for " + type.getName());
            }
            return ClassFile.of().parse(in.readAllBytes());
        } catch (IOException e) {
            throw new UncheckedIOException("cannot read " + fileName, e);
        }
    }

    private static Optional<String> name(ClassDesc descriptor) {
        ClassDesc element = descriptor;
        while (element.isArray()) {
            element = element.componentType();
        }
        if (element.isPrimitive()) {
            return Optional.empty();
        }
        String packageName = element.packageName();
        return Optional.of(packageName.isEmpty() ? element.displayName() : packageName + "." + element.displayName());
    }
}
