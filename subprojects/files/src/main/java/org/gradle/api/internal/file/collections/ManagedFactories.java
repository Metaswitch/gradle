/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.internal.file.collections;

import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.internal.state.ManagedFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Set;

public class ManagedFactories {
    public static class ConfigurableFileCollectionManagedFactory implements ManagedFactory {
        private final FileResolver resolver;

        public ConfigurableFileCollectionManagedFactory(FileResolver resolver) {
            this.resolver = resolver;
        }

        @Nullable
        @Override
        public <T> T fromState(Class<T> type, Object state) {
            if (!canCreate(type)) {
                return null;
            }
            return type.cast(new DefaultConfigurableFileCollection(resolver, null, (Set<File>) state));
        }

        @Override
        public boolean canCreate(Class<?> type) {
            return type.isAssignableFrom(ConfigurableFileCollection.class);
        }
    }
}