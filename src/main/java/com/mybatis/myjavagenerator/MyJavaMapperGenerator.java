package com.mybatis.myjavagenerator;

import java.util.List;

import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.codegen.mybatis3.javamapper.JavaMapperGenerator;

public class MyJavaMapperGenerator extends JavaMapperGenerator {
	@Override
	public List<CompilationUnit> getExtraCompilationUnits() {
		System.out.println("------------------------------------");
		return super.getExtraCompilationUnits();
	}
}
