/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.causeway.core.metamodel.facets.properties.property;

import java.util.regex.Pattern;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.causeway.applib.annotation.MemberSupport;
import org.apache.causeway.applib.annotation.Optionality;
import org.apache.causeway.applib.annotation.Property;
import org.apache.causeway.applib.annotation.PropertyLayout;
import org.apache.causeway.applib.annotation.Publishing;
import org.apache.causeway.applib.annotation.Snapshot;
import org.apache.causeway.applib.annotation.Where;
import org.apache.causeway.applib.events.domain.PropertyDomainEvent;
import org.apache.causeway.applib.spec.Specification;
import org.apache.causeway.commons.collections.Can;
import org.apache.causeway.core.metamodel.commons.matchers.CausewayMatchers;
import org.apache.causeway.core.metamodel.consent.Consent.VetoReason;
import org.apache.causeway.core.metamodel.consent.InteractionInitiatedBy;
import org.apache.causeway.core.metamodel.facetapi.Facet;
import org.apache.causeway.core.metamodel.facetapi.FacetHolder;
import org.apache.causeway.core.metamodel.facetapi.FacetUtil;
import org.apache.causeway.core.metamodel.facets.DomainEventFacetAbstract.EventTypeOrigin;
import org.apache.causeway.core.metamodel.facets.FacetFactory;
import org.apache.causeway.core.metamodel.facets.FacetFactoryTestAbstract;
import org.apache.causeway.core.metamodel.facets.all.hide.HiddenFacet;
import org.apache.causeway.core.metamodel.facets.members.disabled.DisabledFacet;
import org.apache.causeway.core.metamodel.facets.objectvalue.mandatory.MandatoryFacet;
import org.apache.causeway.core.metamodel.facets.objectvalue.maxlen.MaxLengthFacet;
import org.apache.causeway.core.metamodel.facets.objectvalue.mustsatisfyspec.MustSatisfySpecificationFacet;
import org.apache.causeway.core.metamodel.facets.objectvalue.regex.RegExFacet;
import org.apache.causeway.core.metamodel.facets.propcoll.accessor.PropertyOrCollectionAccessorFacetAbstract;
import org.apache.causeway.core.metamodel.facets.propcoll.memserexcl.SnapshotExcludeFacet;
import org.apache.causeway.core.metamodel.facets.properties.property.disabled.DisabledFacetForPropertyAnnotation;
import org.apache.causeway.core.metamodel.facets.properties.property.entitychangepublishing.EntityPropertyChangePublishingPolicyFacet;
import org.apache.causeway.core.metamodel.facets.properties.property.hidden.HiddenFacetForPropertyAnnotation;
import org.apache.causeway.core.metamodel.facets.properties.property.mandatory.MandatoryFacetForPropertyAnnotation;
import org.apache.causeway.core.metamodel.facets.properties.property.maxlength.MaxLengthFacetForPropertyAnnotation;
import org.apache.causeway.core.metamodel.facets.properties.property.modify.PropertyClearFacetForDomainEvent;
import org.apache.causeway.core.metamodel.facets.properties.property.modify.PropertyDomainEventFacet;
import org.apache.causeway.core.metamodel.facets.properties.property.modify.PropertySetterFacetForDomainEvent;
import org.apache.causeway.core.metamodel.facets.properties.property.mustsatisfy.MustSatisfySpecificationFacetForPropertyAnnotation;
import org.apache.causeway.core.metamodel.facets.properties.property.regex.RegExFacetForPropertyAnnotation;
import org.apache.causeway.core.metamodel.facets.properties.property.snapshot.SnapshotExcludeFacetForPropertyAnnotation;
import org.apache.causeway.core.metamodel.facets.properties.update.clear.PropertyClearFacet;
import org.apache.causeway.core.metamodel.facets.properties.update.clear.PropertyClearFacetAbstract;
import org.apache.causeway.core.metamodel.facets.properties.update.modify.PropertySetterFacet;
import org.apache.causeway.core.metamodel.facets.properties.update.modify.PropertySetterFacetAbstract;
import org.apache.causeway.core.metamodel.object.ManagedObject;
import org.apache.causeway.core.metamodel.spec.ObjectSpecification;
import org.apache.causeway.core.metamodel.spec.feature.OneToOneAssociation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.val;

@SuppressWarnings("removal")
class PropertyAnnotationFacetFactoryTest extends FacetFactoryTestAbstract {

    PropertyAnnotationFacetFactory facetFactory;

    private static void processModify(
            final PropertyAnnotationFacetFactory facetFactory, final FacetFactory.ProcessMethodContext processMethodContext) {
        val propertyIfAny = processMethodContext.synthesizeOnMethod(Property.class);
        facetFactory.processModify(processMethodContext, propertyIfAny);
    }

    private static void processHidden(
            final PropertyAnnotationFacetFactory facetFactory, final FacetFactory.ProcessMethodContext processMethodContext) {
        val propertyIfAny = processMethodContext.synthesizeOnMethod(Property.class);
        facetFactory.processHidden(processMethodContext, propertyIfAny);
    }

    private static void processOptional(
            final PropertyAnnotationFacetFactory facetFactory, final FacetFactory.ProcessMethodContext processMethodContext) {
        val propertyIfAny = processMethodContext.synthesizeOnMethod(Property.class);
        facetFactory.processOptional(processMethodContext, propertyIfAny);
    }

    private static void processRegEx(
            final PropertyAnnotationFacetFactory facetFactory, final FacetFactory.ProcessMethodContext processMethodContext) {
        val propertyIfAny = processMethodContext.synthesizeOnMethod(Property.class);
        facetFactory.processRegEx(processMethodContext, propertyIfAny);
    }

    private static void processEditing(
            final PropertyAnnotationFacetFactory facetFactory, final FacetFactory.ProcessMethodContext processMethodContext) {
        val propertyIfAny = processMethodContext.synthesizeOnMethod(Property.class);
        facetFactory.processEditing(processMethodContext, propertyIfAny);
    }

    private static void processMaxLength(
            final PropertyAnnotationFacetFactory facetFactory, final FacetFactory.ProcessMethodContext processMethodContext) {
        val propertyIfAny = processMethodContext.synthesizeOnMethod(Property.class);
        facetFactory.processMaxLength(processMethodContext, propertyIfAny);
    }

    private static void processMustSatisfy(
            final PropertyAnnotationFacetFactory facetFactory, final FacetFactory.ProcessMethodContext processMethodContext) {
        val propertyIfAny = processMethodContext.synthesizeOnMethod(Property.class);
        facetFactory.processMustSatisfy(processMethodContext, propertyIfAny);
    }

    private static void processSnapshot(
            final PropertyAnnotationFacetFactory facetFactory, final FacetFactory.ProcessMethodContext processMethodContext) {
        val propertyIfAny = processMethodContext.synthesizeOnMethod(Property.class);
        facetFactory.processSnapshot(processMethodContext, propertyIfAny);
    }

    private static void processEntityPropertyChangePublishing(
            final PropertyAnnotationFacetFactory facetFactory, final FacetFactory.ProcessMethodContext processMethodContext) {
        val propertyIfAny = processMethodContext.synthesizeOnMethod(Property.class);
        facetFactory.processEntityPropertyChangePublishing(processMethodContext, propertyIfAny);
    }


    @BeforeEach
    final void setUp() throws Exception {
        facetFactory = new PropertyAnnotationFacetFactory(getMetaModelContext());
    }

    @AfterEach
    final void tearDown() throws Exception {
        facetFactory = null;
    }

    @TestInstance(Lifecycle.PER_CLASS)
    static class Modify extends PropertyAnnotationFacetFactoryTest {

        private void addGetterFacet(final FacetHolder holder) {
            val mockOnType = Mockito.mock(ObjectSpecification.class);
            FacetUtil.addFacet(new PropertyOrCollectionAccessorFacetAbstract(mockOnType, holder) {
                @Override
                public Object getProperty(
                        final ManagedObject inObject,
                        final InteractionInitiatedBy interactionInitiatedBy) {
                    return null;
                }
            });
        }

        private void addSetterFacet(final FacetHolder holder) {
            FacetUtil.addFacet(new PropertySetterFacetAbstract(holder) {
                @Override
                public ManagedObject setProperty(
                        final OneToOneAssociation owningAssociation,
                        final ManagedObject inObject,
                        final ManagedObject value,
                        final InteractionInitiatedBy interactionInitiatedBy) {
                    return inObject;
                }
            });
        }

        private void addClearFacet(final FacetHolder holder) {
            FacetUtil.addFacet(new PropertyClearFacetAbstract(holder) {
                @Override
                public ManagedObject clearProperty(
                        final OneToOneAssociation owningProperty,
                        final ManagedObject targetAdapter,
                        final InteractionInitiatedBy interactionInitiatedBy) {
                    return targetAdapter;
                }
            });
        }


        @Test
        void withDeprecatedPostsPropertyChangedEvent_andGetterFacet_andSetterFacet() {

            class Customer {
                class NamedChangedDomainEvent extends PropertyDomainEvent<Customer, String> {}
                @Property(domainEvent = NamedChangedDomainEvent.class)
                @Getter @Setter private String name;
            }

            // given
            propertyScenario(Customer.class, "name", (processMethodContext, facetHolder, facetedMethod)->{

                addGetterFacet(facetedMethod);
                addSetterFacet(facetedMethod);
                addClearFacet(facetedMethod);

                // when
                processModify(facetFactory, processMethodContext);

                // then
                final PropertyDomainEventFacet domainEventFacet = facetedMethod.getFacet(PropertyDomainEventFacet.class);
                assertNotNull(domainEventFacet);
                assertTrue(domainEventFacet instanceof PropertyDomainEventFacet);
                final PropertyDomainEventFacet domainEventFacetImpl = domainEventFacet;
                assertTrue(domainEventFacetImpl.getEventTypeOrigin().isAnnotatedMember());
                assertThat(domainEventFacetImpl.getEventType(), CausewayMatchers.classEqualTo(Customer.NamedChangedDomainEvent.class));

                // then
                final Facet setterFacet = facetedMethod.getFacet(PropertySetterFacet.class);
                assertNotNull(setterFacet);
                assertTrue(setterFacet instanceof PropertySetterFacetForDomainEvent, "unexpected facet: " + setterFacet);
                final PropertySetterFacetForDomainEvent setterFacetImpl = (PropertySetterFacetForDomainEvent) setterFacet;
                assertEquals(EventTypeOrigin.ANNOTATED_MEMBER, setterFacetImpl.getEventTypeOrigin());
                assertThat(setterFacetImpl.getEventType(), CausewayMatchers.classEqualTo(Customer.NamedChangedDomainEvent.class));

                // then
                final Facet clearFacet = facetedMethod.getFacet(PropertyClearFacet.class);
                assertNotNull(clearFacet);
                assertTrue(clearFacet instanceof PropertyClearFacetForDomainEvent);
                final PropertyClearFacetForDomainEvent clearFacetImpl = (PropertyClearFacetForDomainEvent) clearFacet;
                assertEquals(EventTypeOrigin.ANNOTATED_MEMBER, setterFacetImpl.getEventTypeOrigin());
                assertThat(clearFacetImpl.getEventType(), CausewayMatchers.classEqualTo(Customer.NamedChangedDomainEvent.class));
            });
        }


        @Test
        void withPropertyInteractionEvent() {

            class Customer {
                class NamedChangedDomainEvent extends PropertyDomainEvent<Customer, String> {}
                @Property(domainEvent = NamedChangedDomainEvent.class)
                @Getter @Setter private String name;
            }

            // given
            propertyScenario(Customer.class, "name", (processMethodContext, facetHolder, facetedMethod)->{
                addGetterFacet(facetedMethod);
                addSetterFacet(facetedMethod);
                addClearFacet(facetedMethod);

                // when
                processModify(facetFactory, processMethodContext);

                // then
                final Facet domainEventFacet = facetedMethod.getFacet(PropertyDomainEventFacet.class);
                assertNotNull(domainEventFacet);
                assertTrue(domainEventFacet instanceof PropertyDomainEventFacet);
                final PropertyDomainEventFacet domainEventFacetImpl = (PropertyDomainEventFacet) domainEventFacet;
                assertTrue(domainEventFacetImpl.getEventTypeOrigin().isAnnotatedMember());
                assertThat(domainEventFacetImpl.getEventType(), CausewayMatchers.classEqualTo(Customer.NamedChangedDomainEvent.class));

                // then
                final Facet setterFacet = facetedMethod.getFacet(PropertySetterFacet.class);
                assertNotNull(setterFacet);
                assertTrue(setterFacet instanceof PropertySetterFacetForDomainEvent, "unexpected facet: " + setterFacet);
                final PropertySetterFacetForDomainEvent setterFacetImpl = (PropertySetterFacetForDomainEvent) setterFacet;
                assertEquals(EventTypeOrigin.ANNOTATED_MEMBER, setterFacetImpl.getEventTypeOrigin());
                assertThat(setterFacetImpl.getEventType(), CausewayMatchers.classEqualTo(Customer.NamedChangedDomainEvent.class));

                // then
                final Facet clearFacet = facetedMethod.getFacet(PropertyClearFacet.class);
                assertNotNull(clearFacet);
                assertTrue(clearFacet instanceof PropertyClearFacetForDomainEvent);
                final PropertyClearFacetForDomainEvent clearFacetImpl = (PropertyClearFacetForDomainEvent) clearFacet;
                assertEquals(EventTypeOrigin.ANNOTATED_MEMBER, clearFacetImpl.getEventTypeOrigin());
                assertThat(clearFacetImpl.getEventType(), CausewayMatchers.classEqualTo(Customer.NamedChangedDomainEvent.class));
            });
        }

        //@Test  //TODO[CAUSEWAY-3409] WIP
        void withPropertyInteractionEvent_mixedIn() {
            class Customer {
                class NamedChangedDomainEvent extends PropertyDomainEvent<Customer, String> {}
            }
            @Property(domainEvent = Customer.NamedChangedDomainEvent.class)
            @PropertyLayout(named = "name")
            @RequiredArgsConstructor
            @SuppressWarnings("unused")
            class Customer_name {
                final Customer mixee;
                @MemberSupport
                public String prop() { return "mixed-in name"; }
            }

            val scenario = PropertyScenario.builder(Customer.class, "name")
                    .mixinClasses(Can.of(Customer_name.class))
                    .build();

            // given
            propertyScenario(scenario, (processMethodContext, facetHolder, facetedMethod)->{
                addGetterFacet(facetedMethod);
                addSetterFacet(facetedMethod);
                addClearFacet(facetedMethod);

                // when
                processModify(facetFactory, processMethodContext);

                // then
                final Facet domainEventFacet = facetedMethod.getFacet(PropertyDomainEventFacet.class);
                assertNotNull(domainEventFacet);
                assertTrue(domainEventFacet instanceof PropertyDomainEventFacet);
                final PropertyDomainEventFacet domainEventFacetImpl = (PropertyDomainEventFacet) domainEventFacet;
                assertTrue(domainEventFacetImpl.getEventTypeOrigin().isAnnotatedMember());
                assertThat(domainEventFacetImpl.getEventType(), CausewayMatchers.classEqualTo(Customer.NamedChangedDomainEvent.class));

                // then
                final Facet setterFacet = facetedMethod.getFacet(PropertySetterFacet.class);
                assertNotNull(setterFacet);
                assertTrue(setterFacet instanceof PropertySetterFacetForDomainEvent, "unexpected facet: " + setterFacet);
                final PropertySetterFacetForDomainEvent setterFacetImpl = (PropertySetterFacetForDomainEvent) setterFacet;
                assertEquals(EventTypeOrigin.ANNOTATED_MEMBER, setterFacetImpl.getEventTypeOrigin());
                assertThat(setterFacetImpl.getEventType(), CausewayMatchers.classEqualTo(Customer.NamedChangedDomainEvent.class));

                // then
                final Facet clearFacet = facetedMethod.getFacet(PropertyClearFacet.class);
                assertNotNull(clearFacet);
                assertTrue(clearFacet instanceof PropertyClearFacetForDomainEvent);
                final PropertyClearFacetForDomainEvent clearFacetImpl = (PropertyClearFacetForDomainEvent) clearFacet;
                assertEquals(EventTypeOrigin.ANNOTATED_MEMBER, clearFacetImpl.getEventTypeOrigin());
                assertThat(clearFacetImpl.getEventType(), CausewayMatchers.classEqualTo(Customer.NamedChangedDomainEvent.class));
            });
        }

        @Test
        void withPropertyDomainEvent() {

            class Customer {
                class NamedChangedDomainEvent extends PropertyDomainEvent<Customer, String> {
                }
                @Property(domainEvent= NamedChangedDomainEvent.class)
                @Getter @Setter private String name;
            }

            // given
            propertyScenario(Customer.class, "name", (processMethodContext, facetHolder, facetedMethod)->{
                addGetterFacet(facetedMethod);
                addSetterFacet(facetedMethod);
                addClearFacet(facetedMethod);

                // when
                processModify(facetFactory, processMethodContext);

                // then
                final Facet domainEventFacet = facetedMethod.getFacet(PropertyDomainEventFacet.class);
                assertNotNull(domainEventFacet);
                assertTrue(domainEventFacet instanceof PropertyDomainEventFacet);
                final PropertyDomainEventFacet domainEventFacetImpl = (PropertyDomainEventFacet) domainEventFacet;
                assertTrue(domainEventFacetImpl.getEventTypeOrigin().isAnnotatedMember());
                MatcherAssert.assertThat(domainEventFacetImpl.getEventType(), CausewayMatchers.classEqualTo(Customer.NamedChangedDomainEvent.class));

                // then
                final Facet setterFacet = facetedMethod.getFacet(PropertySetterFacet.class);
                assertNotNull(setterFacet);
                assertTrue(setterFacet instanceof PropertySetterFacetForDomainEvent, "unexpected facet: " + setterFacet);
                final PropertySetterFacetForDomainEvent setterFacetImpl = (PropertySetterFacetForDomainEvent) setterFacet;
                assertEquals(EventTypeOrigin.ANNOTATED_MEMBER, setterFacetImpl.getEventTypeOrigin());
                assertThat(setterFacetImpl.getEventType(), CausewayMatchers.classEqualTo(Customer.NamedChangedDomainEvent.class));

                // then
                final Facet clearFacet = facetedMethod.getFacet(PropertyClearFacet.class);
                assertNotNull(clearFacet);
                assertTrue(clearFacet instanceof PropertyClearFacetForDomainEvent);
                final PropertyClearFacetForDomainEvent clearFacetImpl = (PropertyClearFacetForDomainEvent) clearFacet;
                assertEquals(EventTypeOrigin.ANNOTATED_MEMBER, clearFacetImpl.getEventTypeOrigin());
                assertThat(clearFacetImpl.getEventType(), CausewayMatchers.classEqualTo(Customer.NamedChangedDomainEvent.class));
            });
        }

        @Test
        void withDefaultEvent() {

            class Customer {
                @Getter @Setter private String name;
            }

            // given
            assertTrue(getConfiguration()
                    .getApplib().getAnnotation().getDomainObject().getCreatedLifecycleEvent().isPostForDefault());

            propertyScenario(Customer.class, "name", (processMethodContext, facetHolder, facetedMethod)->{
                addGetterFacet(facetedMethod);
                addSetterFacet(facetedMethod);
                addClearFacet(facetedMethod);

                // when
                processModify(facetFactory, processMethodContext);

                // then
                final Facet domainEventFacet = facetedMethod.getFacet(PropertyDomainEventFacet.class);
                assertNotNull(domainEventFacet);
                assertTrue(domainEventFacet instanceof PropertyDomainEventFacet);
                final PropertyDomainEventFacet domainEventFacetImpl = (PropertyDomainEventFacet) domainEventFacet;
                assertTrue(domainEventFacetImpl.getEventTypeOrigin().isDefault());
                assertThat(domainEventFacetImpl.getEventType(), CausewayMatchers.classEqualTo(PropertyDomainEvent.Default.class));

                // then
                final Facet setterFacet = facetedMethod.getFacet(PropertySetterFacet.class);
                assertNotNull(setterFacet);
                assertTrue(setterFacet instanceof PropertySetterFacetForDomainEvent,
                        "unexpected facet: " + setterFacet);
                final PropertySetterFacetForDomainEvent setterFacetImpl = (PropertySetterFacetForDomainEvent) setterFacet;
                assertThat(setterFacetImpl.getEventType(), CausewayMatchers.classEqualTo(PropertyDomainEvent.Default.class));

                // then
                final Facet clearFacet = facetedMethod.getFacet(PropertyClearFacet.class);
                assertNotNull(clearFacet);
                assertTrue(clearFacet instanceof PropertyClearFacetForDomainEvent);
                final PropertyClearFacetForDomainEvent clearFacetImpl = (PropertyClearFacetForDomainEvent) clearFacet;
                assertThat(clearFacetImpl.getEventType(), CausewayMatchers.classEqualTo(PropertyDomainEvent.Default.class));
            });
        }
    }

    @TestInstance(Lifecycle.PER_CLASS) @Deprecated(forRemoval = true, since = "2.0.0-RC2")
    static class Hidden extends PropertyAnnotationFacetFactoryTest {

        @Test
        void withAnnotation() {

            @SuppressWarnings("unused")
            class Customer {
                @Property(hidden = Where.REFERENCES_PARENT)
                @Getter @Setter private String name;
            }

            // given
            propertyScenario(Customer.class, "name", (processMethodContext, facetHolder, facetedMethod)->{
                // when
                processHidden(facetFactory, processMethodContext);

                // then
                final HiddenFacet hiddenFacet = facetedMethod.getFacet(HiddenFacet.class);
                assertNotNull(hiddenFacet);
                assertTrue(hiddenFacet instanceof HiddenFacetForPropertyAnnotation);
                final HiddenFacetForPropertyAnnotation hiddenFacetImpl = (HiddenFacetForPropertyAnnotation) hiddenFacet;
                assertThat(hiddenFacetImpl.where(), is(Where.REFERENCES_PARENT));

                final Facet hiddenFacetForProp = facetedMethod.getFacet(HiddenFacet.class);
                assertNotNull(hiddenFacetForProp);
                assertTrue(hiddenFacet == hiddenFacetForProp);
            });
        }

    }

    @TestInstance(Lifecycle.PER_CLASS)
    static class Editing extends PropertyAnnotationFacetFactoryTest {

        @Test
        void withAnnotationOnGetter() {
            @SuppressWarnings("unused")
            class Customer {
                @Property(
                        editing = org.apache.causeway.applib.annotation.Editing.DISABLED,
                        editingDisabledReason = "you cannot edit the name property")
                public String getName() { return null; }
                public void setName(final String name) {}
            }

            assertDisabledFacetOn(Customer.class, "name",
                    "you cannot edit the name property");
        }

        @Test
        void withAnnotationOnField() {

            class Customer {
                @Property(
                        editing = org.apache.causeway.applib.annotation.Editing.DISABLED,
                        editingDisabledReason = "you cannot edit the name property")
                @Getter @Setter
                private String name;
            }

            assertDisabledFacetOn(Customer.class, "name",
                    "you cannot edit the name property");
        }


        @Test
        void withAnnotationOnBooleanGetter() {
            @SuppressWarnings("unused")
            class Customer {
                @Property(
                        editing = org.apache.causeway.applib.annotation.Editing.DISABLED,
                        editingDisabledReason = "you cannot edit the subscribed property"
                        )
                public boolean isSubscribed() { return true; }
                public void setSubscribed(final boolean b) {}
            }

            assertDisabledFacetOn(Customer.class, "subscribed",
                    "you cannot edit the subscribed property");
        }

        @Test
        void withAnnotationOnBooleanField() {

            class Customer {
                @Property(
                        editing = org.apache.causeway.applib.annotation.Editing.DISABLED,
                        editingDisabledReason = "you cannot edit the subscribed property"
                        )
                @Getter @Setter
                private boolean subscribed;
            }

            assertDisabledFacetOn(Customer.class, "subscribed",
                    "you cannot edit the subscribed property");
        }

        // -- SPECIAL SCENARIO CAUSEWAY-2963

        static interface PrimitiveBooleanHolder {
            @Property(
                    editing = org.apache.causeway.applib.annotation.Editing.DISABLED,
                    editingDisabledReason = "a")
            boolean isReadWriteProperty();
            void setReadWriteProperty(boolean c);
        }

        static class PrimitiveBooleanEntity implements PrimitiveBooleanHolder {
            @Property(
                    editing = org.apache.causeway.applib.annotation.Editing.DISABLED,
                    editingDisabledReason = "b")
            @Getter @Setter
            private boolean readWriteProperty;
        }

        @Test
        void recognizeAnnotationOnPrimitiveBoolean() {
            assertDisabledFacetOn(PrimitiveBooleanEntity.class, "readWriteProperty", "b");
        }

        // -- HELPER

        private void assertDisabledFacetOn(final Class<?> declaringClass, final String propertyName, final String expectedDisabledReason) {

            // given
            propertyScenario(declaringClass, propertyName, (processMethodContext, facetHolder, facetedMethod)->{
                // when
                processEditing(facetFactory, processMethodContext);
                // then
                val disabledFacet = facetedMethod.getFacet(DisabledFacet.class);
                assertNotNull(disabledFacet);
                assertTrue(disabledFacet instanceof DisabledFacetForPropertyAnnotation);
                val disabledFacet2 = (DisabledFacetForPropertyAnnotation) disabledFacet;
                assertThat(disabledFacet.where(), is(Where.EVERYWHERE));
                assertThat(disabledFacet2.disabledReason(null).map(VetoReason::string).orElse(null), is(expectedDisabledReason));
            });
        }

    }

    @TestInstance(Lifecycle.PER_CLASS)
    static class MaxLength extends PropertyAnnotationFacetFactoryTest {

        @Test
        void withAnnotation() {

            class Customer {
                @Property(maxLength = 30)
                @Getter @Setter private String name;
            }

            // given
            propertyScenario(Customer.class, "name", (processMethodContext, facetHolder, facetedMethod)->{
                // when
                processMaxLength(facetFactory, processMethodContext);

                // then
                final MaxLengthFacet maxLengthFacet = facetedMethod.getFacet(MaxLengthFacet.class);
                assertNotNull(maxLengthFacet);
                assertTrue(maxLengthFacet instanceof MaxLengthFacetForPropertyAnnotation);
                assertThat(maxLengthFacet.value(), is(30));
            });
        }
    }

    @TestInstance(Lifecycle.PER_CLASS)
    static class MustSatisfy extends PropertyAnnotationFacetFactoryTest {

        public static class NotTooHot implements Specification {
            @Override
            public String satisfies(final Object obj) {
                return null;
            }
        }

        public static class NotTooCold implements Specification {
            @Override
            public String satisfies(final Object obj) {
                return null;
            }
        }


        @Test
        void withAnnotation() {

            class Customer {
                @Property(
                        mustSatisfy = {NotTooHot.class, NotTooCold.class}
                        )
                @Getter @Setter private String name;
            }

            // given
            propertyScenario(Customer.class, "name", (processMethodContext, facetHolder, facetedMethod)->{
                // when
                processMustSatisfy(facetFactory, processMethodContext);

                // then
                final MustSatisfySpecificationFacet mustSatisfySpecificationFacet = facetedMethod.getFacet(MustSatisfySpecificationFacet.class);
                assertNotNull(mustSatisfySpecificationFacet);
                assertTrue(mustSatisfySpecificationFacet instanceof MustSatisfySpecificationFacetForPropertyAnnotation);
                final MustSatisfySpecificationFacetForPropertyAnnotation mustSatisfySpecificationFacetImpl = (MustSatisfySpecificationFacetForPropertyAnnotation) mustSatisfySpecificationFacet;
                val specifications = mustSatisfySpecificationFacetImpl.getSpecifications();
                assertThat(specifications.size(), is(2));

                assertTrue(specifications.getElseFail(0) instanceof NotTooHot);
                assertTrue(specifications.getElseFail(1) instanceof NotTooCold);
            });
        }

    }

    @TestInstance(Lifecycle.PER_CLASS)
    static class EntityPropertyChangePublishingPolicy extends PropertyAnnotationFacetFactoryTest {

        @Test
        void exclusion() {

            class Customer {
                @Property(entityChangePublishing = Publishing.DISABLED)
                @Getter @Setter private String name;
            }

            // given
            propertyScenario(Customer.class, "name", (processMethodContext, facetHolder, facetedMethod)->{
                // when
                processEntityPropertyChangePublishing(facetFactory, processMethodContext);
                // then
                val changePolicyFacet = facetedMethod.getFacet(EntityPropertyChangePublishingPolicyFacet.class);
                assertNotNull(changePolicyFacet);
                assertTrue(changePolicyFacet.isPublishingVetoed());
                assertFalse(changePolicyFacet.isPublishingAllowed());
            });
        }

        @Test
        void whenDefault() {

            class Customer {
                @Property
                @Getter @Setter private String name;
            }

            // given
            propertyScenario(Customer.class, "name", (processMethodContext, facetHolder, facetedMethod)->{
                // when
                processEntityPropertyChangePublishing(facetFactory, processMethodContext);
                // then
                val changePolicyFacet = facetedMethod.getFacet(EntityPropertyChangePublishingPolicyFacet.class);
                assertNull(changePolicyFacet);
            });
        }

    }

    @TestInstance(Lifecycle.PER_CLASS)
    static class SnapshotExcluded extends PropertyAnnotationFacetFactoryTest {

        @Test
        void withAnnotation() {

            class Customer {
                @Property(snapshot = Snapshot.EXCLUDED)
                @Getter @Setter private String name;
            }

            // given
            propertyScenario(Customer.class, "name", (processMethodContext, facetHolder, facetedMethod)->{
                // when
                processSnapshot(facetFactory, processMethodContext);
                // then
                final SnapshotExcludeFacet snapshotExcludeFacet = facetedMethod.getFacet(SnapshotExcludeFacet.class);
                assertNotNull(snapshotExcludeFacet);
                assertTrue(snapshotExcludeFacet instanceof SnapshotExcludeFacetForPropertyAnnotation);
            });
        }
    }

    @TestInstance(Lifecycle.PER_CLASS)
    static class Mandatory extends PropertyAnnotationFacetFactoryTest {

        @Test
        void whenOptionalityIsTrue() {

            class Customer {
                @Property(optionality = Optionality.OPTIONAL)
                @Getter @Setter private String name;
            }

            // given
            propertyScenario(Customer.class, "name", (processMethodContext, facetHolder, facetedMethod)->{
                // when
                processOptional(facetFactory, processMethodContext);
                // then
                final MandatoryFacet mandatoryFacet = facetedMethod.getFacet(MandatoryFacet.class);
                assertNotNull(mandatoryFacet);
                assertTrue(mandatoryFacet instanceof MandatoryFacetForPropertyAnnotation.Optional);
            });
        }

        @Test
        void whenOptionalityIsFalse() {

            class Customer {
                @Property(optionality = Optionality.MANDATORY)
                @Getter @Setter private String name;
            }

            // given
            propertyScenario(Customer.class, "name", (processMethodContext, facetHolder, facetedMethod)->{
                // when
                processOptional(facetFactory, processMethodContext);
                // then
                final MandatoryFacet mandatoryFacet = facetedMethod.getFacet(MandatoryFacet.class);
                assertNotNull(mandatoryFacet);
                assertTrue(mandatoryFacet instanceof MandatoryFacetForPropertyAnnotation.Required);
            });
        }

        @Test
        void whenOptionalityIsDefault() {

            class Customer {
                @Property(optionality = Optionality.DEFAULT)
                @Getter @Setter private String name;
            }

            // given
            propertyScenario(Customer.class, "name", (processMethodContext, facetHolder, facetedMethod)->{
                // when
                processOptional(facetFactory, processMethodContext);
                // then
                final MandatoryFacet mandatoryFacet = facetedMethod.getFacet(MandatoryFacet.class);
                assertNull(mandatoryFacet);
            });
        }

        @Test
        void whenNone() {

            class Customer {
                @Property()
                @Getter @Setter private String name;
            }

            // given
            propertyScenario(Customer.class, "name", (processMethodContext, facetHolder, facetedMethod)->{
                // when
                processOptional(facetFactory, processMethodContext);
                // then
                final MandatoryFacet mandatoryFacet = facetedMethod.getFacet(MandatoryFacet.class);
                assertNull(mandatoryFacet);
            });
        }

    }

    @TestInstance(Lifecycle.PER_CLASS)
    static class RegEx extends PropertyAnnotationFacetFactoryTest {

        @Test
        void whenHasAnnotation() {

            class Customer {
                @Property(
                        regexPattern = "[123].*",
                        regexPatternFlags = Pattern.CASE_INSENSITIVE | Pattern.MULTILINE)
                @Getter @Setter private String name;
            }

            // given
            propertyScenario(Customer.class, "name", (processMethodContext, facetHolder, facetedMethod)->{
                // when
                processRegEx(facetFactory, processMethodContext);
                // then
                final RegExFacet regExFacet = facetedMethod.getFacet(RegExFacet.class);
                assertNotNull(regExFacet);
                assertTrue(regExFacet instanceof RegExFacetForPropertyAnnotation);
                assertThat(regExFacet.patternFlags(), is(10));
                assertThat(regExFacet.regexp(), is("[123].*"));
            });
        }

        @Test
        void whenNone() {

            class Customer {
                @Property()
                @Getter @Setter private String name;
            }

            // given
            propertyScenario(Customer.class, "name", (processMethodContext, facetHolder, facetedMethod)->{
                // when
                processRegEx(facetFactory, processMethodContext);
                // then
                final RegExFacet regExFacet = facetedMethod.getFacet(RegExFacet.class);
                assertNull(regExFacet);
            });
        }

        @Test
        void whenEmptyString() {

            class Customer {
                @Property(regexPattern = "")
                @Getter @Setter private String name;
            }

            // given
            propertyScenario(Customer.class, "name", (processMethodContext, facetHolder, facetedMethod)->{
                // when
                processRegEx(facetFactory, processMethodContext);

                // then
                final RegExFacet regExFacet = facetedMethod.getFacet(RegExFacet.class);
                assertNull(regExFacet);
            });
        }

        @Test
        void whenNotAnnotatedOnStringProperty() {

            class Customer {
                @Property(regexPattern = "[abc].*")
                public int getName() {return 0; }
                @SuppressWarnings("unused") public void setName(final int name) { }
            }

            // given
            propertyScenario(Customer.class, "name", (processMethodContext, facetHolder, facetedMethod)->{
                // when
                processRegEx(facetFactory, processMethodContext);

                // then
                final RegExFacet regExFacet = facetedMethod.getFacet(RegExFacet.class);
                assertNull(regExFacet);
            });
        }

    }

}
