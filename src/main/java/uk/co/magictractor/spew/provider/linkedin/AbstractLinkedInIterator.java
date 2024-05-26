/**
 * Copyright 2019 Ken Dobson
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
package uk.co.magictractor.spew.provider.linkedin;

import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.api.StartAtServiceIterator;

public abstract class AbstractLinkedInIterator<E> extends StartAtServiceIterator<E> {

    public static class AbstractLinkedInIteratorBuilder<E, I extends AbstractLinkedInIterator<E>, B>
            extends StartAtServiceIteratorBuilder<E, I, B> {

        protected AbstractLinkedInIteratorBuilder(SpewApplication<?> application, Class<E> elementType,
                I iteratorInstance) {
            super(application, elementType, iteratorInstance);
        }

    }

}
