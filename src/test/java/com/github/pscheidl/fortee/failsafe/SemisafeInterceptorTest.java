package com.github.pscheidl.fortee.failsafe;

import com.github.pscheidl.fortee.failsafe.beans.SemiGuardedBean;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.MavenImporter;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.Optional;

@RunWith(Arquillian.class)
public class SemisafeInterceptorTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Inject
    private SemiGuardedBean semiGuardedBean;

    @Deployment
    public static WebArchive createDeployment() {

        final JavaArchive as = ShrinkWrap.create(MavenImporter.class)
                .loadPomFromFile("pom.xml")
                .importBuildOutput()
                .as(JavaArchive.class);

        return ShrinkWrap.create(WebArchive.class)
                .addAsLibrary(as)
                .addClass(ExceptionUtils.class)
                .addClass(SemiGuardedBean.class)
                .addAsWebInfResource("beans.xml");
    }

    @Test
    public void testLetThrough() {
        expectedException.expect(AssertionError.class);
        semiGuardedBean.letThrough();
    }

    @Test
    public void testNotLetThrough() {
        final Optional<String> optionalReturnValue = semiGuardedBean.doNotLetThrough();
        Assert.assertNotNull(optionalReturnValue);
        Assert.assertFalse(optionalReturnValue.isPresent());
    }

    @Test
    public void testInheritance() {
        expectedException.expect(AssertionError.class);
        semiGuardedBean.letInheritedThrough();
    }

    @Test
    public void testConvertNull() {
        Optional<String> optionalWithStringInside = semiGuardedBean.returnNull();
        Assert.assertNotNull(optionalWithStringInside);
        Assert.assertFalse(optionalWithStringInside.isPresent());
    }

    @Test
    public void testValueReturned() {
        Optional<String> optionalWithStringInside = semiGuardedBean.returnSomething();
        Assert.assertNotNull(optionalWithStringInside);
        Assert.assertTrue(optionalWithStringInside.isPresent());
        Assert.assertEquals("Something", optionalWithStringInside.get());
    }

    @Test
    public void testThrowSilentException() {
        expectedException.expect(RuntimeException.class);
        semiGuardedBean.throwSilentException();
    }

    @Test
    public void testConvertSilentException() {
        semiGuardedBean.convertSilentException();
    }

}
