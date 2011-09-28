package com.google.appengine.tools.mapreduce;
/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import org.apache.hadoop.conf.Configuration;

/**
 * MapperFactory is a Factory class that is responsible for the creation of the Mapper classes that are defined in the configuration.
 *
 * @author Miroslav Genov (mgenov@gmail.com)
 */
public interface MapperFactory {

  /**
   * Creates a {@link AppEngineMapper} for this task invocation.
   *
   * @param <INKEY> the type of the input keys for this mapper
   * @param <INVALUE> the type of the input values for this mapper
   * @param <OUTKEY> the type of the output keys for this mapper
   * @param <OUTVALUE> the type of the output values for this mapper
   * @return the new mapper
   */
   <INKEY,INVALUE,OUTKEY,OUTVALUE> AppEngineMapper<INKEY,INVALUE,OUTKEY,OUTVALUE> createMapper(Class<? extends org.apache.hadoop.mapreduce.Mapper<?,?,?,?>> mapperClass, Configuration configuration);
}
