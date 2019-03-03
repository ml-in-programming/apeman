/*
 * Copyright 2005, Sixth and Red River Software
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package apeman_core.features_extraction.calculators.method;

import apeman_core.base_entities.FeatureType;
import apeman_core.pipes.CandidateWithFeatures;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiTypeElement;

import java.util.ArrayList;

public class NumTypedElementsMethodCalculator extends NumSimpleElementMethodCalculator {

    public NumTypedElementsMethodCalculator(ArrayList<CandidateWithFeatures> candidates) {
        super(candidates, FeatureType.CON_TYPED_ELEMENTS);
    }

    @Override
    public JavaRecursiveElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends NumSimpleElementMethodCalculator.Visitor {

        @Override
        public void visitTypeElement(PsiTypeElement type) {
            super.visitTypeElement(type);
            elementsCounter++;
        }
    }
}
