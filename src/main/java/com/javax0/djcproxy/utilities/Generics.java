package com.javax0.djcproxy.utilities;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashSet;
import java.util.Set;

/**
 * Class borrowed from http://blog.vityuk.com/2011/03/java-generics-and-reflection.html
 *
 */
public class Generics {
	public static String typeToString(Type type) {
		StringBuilder sb = new StringBuilder();
		typeToString(sb, type, new HashSet<Type>());
		return sb.toString();
	}

	private static void typeToString(StringBuilder sb, Type type,
			Set<Type> visited) {
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			final Class<?> rawType = (Class<?>) parameterizedType.getRawType();
			sb.append(rawType.getName());
			boolean first = true;
			for (Type typeArg : parameterizedType.getActualTypeArguments()) {
				if (first) {
					first = false;
				} else {
					sb.append(", ");
				}
				sb.append('<');
				typeToString(sb, typeArg, visited);
				sb.append('>');
			}
		} else if (type instanceof WildcardType) {
			WildcardType wildcardType = (WildcardType) type;
			sb.append("?");

			/*
			 * According to
			 * JLS(http://java.sun.com/docs/books/jls/third_edition/
			 * html/typesValues.html#4.5.1): - Lower and upper can't coexist:
			 * (for instance, this is not allowed: <? extends List<String> &
			 * super MyInterface>) - Multiple bounds are not supported (for
			 * instance, this is not allowed: <? extends List<String> &
			 * MyInterface>)
			 */
			final Type bound;
			if (wildcardType.getLowerBounds().length != 0) {
				sb.append(" super ");
				bound = wildcardType.getLowerBounds()[0];
			} else {
				sb.append(" extends ");
				bound = wildcardType.getUpperBounds()[0];
			}
			typeToString(sb, bound, visited);
		} else if (type instanceof TypeVariable<?>) {
			TypeVariable<?> typeVariable = (TypeVariable<?>) type;
			sb.append(typeVariable.getName());
			/*
			 * Prevent cycles in case: <T extends List<T>>
			 */
			if (!visited.contains(type)) {
				visited.add(type);
				sb.append(" extends ");
				boolean first = true;
				for (Type bound : typeVariable.getBounds()) {
					if (first) {
						first = false;
					} else {
						sb.append(" & ");
					}
					typeToString(sb, bound, visited);
				}
				visited.remove(type);
			}
		} else if (type instanceof GenericArrayType) {
			GenericArrayType genericArrayType = (GenericArrayType) type;
			typeToString(genericArrayType.getGenericComponentType());
			sb.append(genericArrayType.getGenericComponentType());
			sb.append("[]");
		} else if (type instanceof Class) {
			Class<?> typeClass = (Class<?>) type;
			sb.append(typeClass.getName());
		} else {
			throw new IllegalArgumentException("Unsupported type: " + type);
		}
	}
}
