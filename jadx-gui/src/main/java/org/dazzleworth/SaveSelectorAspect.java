package org.dazzleworth;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.List;

import jadx.api.*;
import jadx.gui.JadxWrapper;

@Aspect
public class SaveSelectorAspect {

	@Around("JadxDecompiler.getClasses()")
	public Object checkClassSel(ProceedingJoinPoint jp) throws Throwable {
		List<JavaClass> jclass = JadxWrapper.getSelectedClasses();

		if(jclass.size() > 0) return jclass;

		return jp.proceed();
	}

	@Around("JadxDecompiler.getResources()")
	public Object checkResSel(ProceedingJoinPoint jp) throws Throwable {
		List<ResourceFile> jres = JadxWrapper.getSelectedResources();

		if(jres.size() > 0) return jres;

		return jp.proceed();
	}
}