package com.google.appengine.tools.mapreduce;

import com.google.inject.Inject;
import com.google.inject.Injector;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.util.ReflectionUtils;

/**
 * InjectingMapReduceServlet is a MapReduceServlet that is using Guice to instantiate mapper class, so the dependencies that
 * are marked with {@literal@}Inject. To use this servlet in your project, you have to define it in the servlet module as:
 * <pre>
 *   serve("/mapreduce/*").with(InjectingMapReduceServlet.class);
     bind(InjectingMapReduceServlet.class).in(Singleton.class);
 * </pre>
 */
public class InjectingMapReduceServlet extends MapReduceServlet {

  private static MapperFactory mapperFactory = null;

  private final Injector injector;

  @Inject
  public InjectingMapReduceServlet(Injector injector) {
    this.injector = injector;
  }

  @Override
  protected MapperFactory getMapperFactory() {

    if (mapperFactory == null) {
      mapperFactory = new MapperFactory() {
        public <INKEY, INVALUE, OUTKEY, OUTVALUE> AppEngineMapper<INKEY, INVALUE, OUTKEY, OUTVALUE> createMapper(Class<? extends Mapper<?, ?, ?, ?>> mapperClass, Configuration configuration) {
          AppEngineMapper<INKEY, INVALUE, OUTKEY, OUTVALUE> mapper = (AppEngineMapper<INKEY, INVALUE, OUTKEY, OUTVALUE>) injector.getInstance(mapperClass);

          /**
           * We need to be sure that configuration is set in the mapper to be at 100% compatible with the {@link ReflectionUtils#newInstance(Class, org.apache.hadoop.conf.Configuration)}.
           */
          ReflectionUtils.setConf(mapper, configuration);

          return mapper;
        }
      };
    }
    return mapperFactory;
  }
}

