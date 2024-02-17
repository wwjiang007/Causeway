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
package org.apache.causeway.viewer.graphql.viewer.test.e2e.queryandmutations;

import java.util.List;

import org.approvaltests.Approvals;
import org.approvaltests.reporters.DiffReporter;
import org.approvaltests.reporters.UseReporter;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;

import org.apache.causeway.viewer.graphql.viewer.test.domain.dept.Department;
import org.apache.causeway.viewer.graphql.viewer.test.domain.dept.DeptHead;
import org.apache.causeway.viewer.graphql.viewer.test.e2e.Abstract_IntegTest;

import lombok.val;


//NOT USING @Transactional since we are running server within same transaction otherwise
@Order(40)
@ActiveProfiles("test")
public class Department_IntegTest extends Abstract_IntegTest {

    @Test
    @UseReporter(DiffReporter.class)
    void find_all_departments() throws Exception {

        // given
        transactionService.runTransactional(Propagation.REQUIRED, () -> {

            departmentRepository.create("foo", null);
            departmentRepository.create("bar", null);
            transactionService.flushTransaction();

            List<Department> allDepartment = departmentRepository.findAll();
            assertThat(allDepartment).hasSize(2);

            List<DeptHead> allDeptHead = deptHeadRepository.findAll();
            assertThat(allDeptHead).isEmpty();
        });

        // when, then
        Approvals.verify(submit(), jsonOptions());
    }

    @Test
    @UseReporter(DiffReporter.class)
    void find_department_by_name() throws Exception {

        // when, then
        Approvals.verify(submit(), jsonOptions());
    }

    @Test
    @UseReporter(DiffReporter.class)
    void find_department_and_change_name_invalid() throws Exception {

        // when, then
        Approvals.verify(submit(), jsonOptions());
    }

    @Test
    @UseReporter(DiffReporter.class)
    void find_department_and_change_head_default() throws Exception {

        // when, then
        Approvals.verify(submit(), jsonOptions());
    }

    @Test
    @UseReporter(DiffReporter.class)
    void find_department_and_edit_head_autocomplete() throws Exception {

        // when, then
        Approvals.verify(submit(), jsonOptions());
    }

    @Test
    @UseReporter(DiffReporter.class)
    void find_department_and_edit_head_autocomplete_none_matching() throws Exception {

        // when, then
        Approvals.verify(submit(), jsonOptions());
    }

    @Test
    @UseReporter(DiffReporter.class)
    void find_department_and_add_staff_member_choices() throws Exception {

        // when, then
        Approvals.verify(submit(), jsonOptions());

    }

    @Test
    @UseReporter(DiffReporter.class)
    void find_department_and_add_staff_members() throws Exception {

        // when, then
        Approvals.verify(submit(), jsonOptions());

    }

    @Test
    @UseReporter(DiffReporter.class)
    void find_department_and_change_head_autocomplete() throws Exception {

        // when, then
        Approvals.verify(submit(), jsonOptions());
    }

    @Test
    @UseReporter(DiffReporter.class)
    void create_department() throws Exception {

        // given
        final int numDepartmentsInitially =
                transactionService.callTransactional(
                        Propagation.REQUIRED,
                        () -> departmentRepository.findAll().size()
                ).valueAsNonNullElseFail();

        // when
        String newDepartmentName = "newbie";
        val response = submit();

        // then payload
        Approvals.verify(response, jsonOptions());

        // and also in the database
        final List<Department> allDepartment =
                transactionService.callTransactional(
                        Propagation.REQUIRED,
                        () -> departmentRepository.findAll()
                ).valueAsNonNullElseFail();

        assertThat(allDepartment)
                .hasSize(numDepartmentsInitially + 1)
                .filteredOn(x -> x.getName().equals(newDepartmentName))
                .extracting(Department::getName)
                .first()
                .isEqualTo(newDepartmentName);

        final Department newbieDepartment =
                transactionService.callTransactional(
                        Propagation.REQUIRED,
                        () -> departmentRepository.findByName(newDepartmentName)
                ).valueAsNullableElseFail();

        assertThat(newbieDepartment).isNotNull();
    }

    @Test
    @UseReporter(DiffReporter.class)
    void create_department_name_param_disabled() throws Exception {

        // when
        val response = submit();

        // then payload
        Approvals.verify(response, jsonOptions());
    }


    @Test
    @UseReporter(DiffReporter.class)
    void find_department_and_remove_staff_member_choices() throws Exception {

        // when, then
        Approvals.verify(submit(), jsonOptions());
    }

    @Test
    @UseReporter(DiffReporter.class)
    void find_department_and_list_its_staff() throws Exception {

        // when, then
        Approvals.verify(submit(), jsonOptions());
    }


}