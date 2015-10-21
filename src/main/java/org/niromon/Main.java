package org.niromon;

import com.github.javafaker.Faker;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.*;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by arnaud on 2015-10-20.
 */
public class Main {

    static class FakeConstructor extends SafeConstructor {

        public FakeConstructor() {
            this.yamlConstructors.put(new Tag("!fake"), new ConstructFake());
        }

        private class ConstructFake extends AbstractConstruct {

            private Faker faker;

            public ConstructFake() {
                this.faker = new Faker();
            }

            public Object construct(Node node) {
                String fakerName = (String) constructScalar((ScalarNode) node);
                String[] fakerMethods = fakerName.split("\\.");
                Object obj = this.faker;
                try {
                    for (int i = 0; i < fakerMethods.length; i++) {
                        obj = obj.getClass().getMethod(fakerMethods[i], null).invoke(obj, null);
                    }
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                return obj;
            }
        }
    }

    public static void main(String [] args) throws FileNotFoundException {
        Yaml yaml = new Yaml(new FakeConstructor());
        Object object = yaml.load("test: !fake 'address.secondaryAddress'");
        System.out.println(object);
    }
}
