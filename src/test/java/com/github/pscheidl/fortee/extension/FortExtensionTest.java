package com.github.pscheidl.fortee.extension;

import com.github.pscheidl.fortee.extension.beans.IncorrectFailsafeMethodContractBean;
import com.github.pscheidl.fortee.extension.beans.IncorrectSemisafeMethodContractBean;
import com.github.pscheidl.fortee.extension.beans.OneMethodWithIncorrectFailsafeContractBean;
import com.github.pscheidl.fortee.extension.beans.OneMethodWithIncorrectSemisafeContractBean;
import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.MavenImporter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class FortExtensionTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @ArquillianResource
    private Deployer deployer;

    @Deployment(managed = false, name = "FAILING_GUARDED_METHOD")
    public static WebArchive createGuardedMethodDeployment() {

        final JavaArchive as = ShrinkWrap.create(MavenImporter.class)
                .loadPomFromFile("pom.xml")
                .importBuildOutput()
                .as(JavaArchive.class);

        return ShrinkWrap.create(WebArchive.class)
                .addAsLibrary(as)
                .addClass(IncorrectFailsafeMethodContractBean.class)
                .addAsWebInfResource("beans.xml");
    }

    @Deployment(managed = false, name = "FAILING_GUARDED_BEAN")
    public static WebArchive createGuardedBeanDeployment() {

        final JavaArchive as = ShrinkWrap.create(MavenImporter.class)
                .loadPomFromFile("pom.xml")
                .importBuildOutput()
                .as(JavaArchive.class);

        return ShrinkWrap.create(WebArchive.class)
                .addAsLibrary(as)
                .addClass(OneMethodWithIncorrectFailsafeContractBean.class)
                .addAsWebInfResource("beans.xml");
    }

    @Deployment(managed = false, name = "FAILING_SEMI_GUARDED_METHOD")
    public static WebArchive createSemiGuardedMethodDeployment() {

        final JavaArchive as = ShrinkWrap.create(MavenImporter.class)
                .loadPomFromFile("pom.xml")
                .importBuildOutput()
                .as(JavaArchive.class);

        return ShrinkWrap.create(WebArchive.class)
                .addAsLibrary(as)
                .addClass(IncorrectSemisafeMethodContractBean.class)
                .addAsWebInfResource("beans.xml");
    }

    @Deployment(managed = false, name = "FAILING_SEMI_GUARDED_BEAN")
    public static WebArchive createSemiGuardedBeanDeployment() {

        final JavaArchive as = ShrinkWrap.create(MavenImporter.class)
                .loadPomFromFile("pom.xml")
                .importBuildOutput()
                .as(JavaArchive.class);

        return ShrinkWrap.create(WebArchive.class)
                .addAsLibrary(as)
                .addClass(OneMethodWithIncorrectSemisafeContractBean.class)
                .addAsWebInfResource("beans.xml");
    }

    @Test
    public void testSingleMethodGuarded() {
        expectedException.expect(DeploymentException.class);
        deployer.deploy("FAILING_GUARDED_METHOD");
    }

    @Test
    public void testGuardedBean() {
        expectedException.expect(DeploymentException.class);
        deployer.deploy("FAILING_GUARDED_BEAN");
    }

    @Test
    public void testSingleMethodSemoGuarded() {
        expectedException.expect(DeploymentException.class);
        deployer.deploy("FAILING_SEMI_GUARDED_METHOD");
    }

    @Test
    public void testSemiGuardedBean() {
        expectedException.expect(DeploymentException.class);
        deployer.deploy("FAILING_SEMI_GUARDED_BEAN");
    }
}
