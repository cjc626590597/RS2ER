<template>
    <div class="common-layout">
        <el-container>
            <el-main>
                <div class="selections">
                    <h1>Please choose entity: </h1>
                    <el-radio-group v-model="selectedRelation" @change="chooseRelation">
                        <div v-for="(value, index) in allRelations" :key="index">
                            <div class="relation">
                                <el-radio :label="value.name" v-show="!value.junctionRelation" border>{{value.name}}
                                </el-radio>
                            </div>
                        </div>
                    </el-radio-group>
                </div>
                <div class="selections" v-show="isRelationSelected">
                    <h1>Please choose only attributes or schema patterns: </h1>
                    <el-radio-group v-model="choice" @change="choose">
                        <el-radio label="attributes" border></el-radio>
                        <el-radio label="schema patterns" border></el-radio>
                    </el-radio-group>
                </div>
                <div class="selections" v-show="choice==='attributes'">
                    <h1>Please choose attributes: </h1>
                    <el-checkbox-group v-model="selectedAttributes" @change="chooseAttributes"
                                       style="display: inline-flex;">
                        <div v-for="(attr, index) in thisAttributes" :key="index">
                            <el-checkbox :label="attr" border>{{attr}}</el-checkbox>
                        </div>
                    </el-checkbox-group>
                </div>
                <div class="selections" v-show="choice==='schema patterns'">
                    <h1>Please choose relationship: </h1>
                    <el-radio-group v-model="selectedRelationShip" @change="chooseRelationship">
                        <div v-for="(rs, index) in basicEntityRelationShips" :key="index">
                            <el-radio :label="rs.relation1+'1'" border>{{rs.relation1}} (basic-entity)</el-radio>
                        </div>
                        <div v-for="(rs, index) in weakEntityRelationShips" :key="index">
                            <el-radio :label="rs.relation1+'2'" border>{{rs.relation1}} (weak-entity)</el-radio>
                        </div>
                        <div v-for="(rs, index) in oneManyRelationShips" :key="index">
                            <el-radio :label="rs.relation1+'3'" border>{{rs.relation1}} (one-many)</el-radio>
                        </div>
                        <div v-for="(rs, index) in manyManyRelationShips" :key="index">
                            <el-radio :label="rs.relation1+'4'" border>{{rs.relation1}} (many-many)</el-radio>
                        </div>
                    </el-radio-group>
                </div>
                <div class="selections" v-show="isRelationShipSelected">
                    <h1>Please choose attributes: </h1>
                    <el-checkbox-group v-model="selectedAttributes" @change="chooseAttributes"
                                       style="display: inline-flex;">
                        <div v-for="(attr, index) in otherAttributes" :key="index">
                            <el-checkbox :label="attr" border>{{attr}}</el-checkbox>
                        </div>
                    </el-checkbox-group>
                </div>
                <div class="submitButton" v-show="isAttributesSelected">
                    <el-button type="primary" @click="recommend" color="green" size="large" :icon="Search">Match
                        visualisation type
                    </el-button>
                </div>
                <div class="selections" v-show="isRecommendationFinished" style="margin-bottom: 10px">
                    <h1>Please choose recommended visualisation type: </h1>
                    <el-radio-group v-model="selectedVisualisation" @change="chooseVisualisation">
                        <div v-for="(value, index) in recommendTypes" :key="index">
                            <div class="relation">
                                <el-radio :label="value" border>{{value}}</el-radio>
                            </div>
                        </div>
                    </el-radio-group>
                </div>
                <div class="selections" v-show="conditions.length!==0">
                    <h1 style="margin-bottom: 0">Filter conditions: </h1>
                    <el-table :data="conditions" style="width: 80%">
                        <el-table-column prop="choice" width="150"/>
                        <el-table-column prop="type" width="120"/>
                        <el-table-column prop="anotherRelation" width="180"/>
                        <el-table-column prop="attribute" width="150"/>
                        <el-table-column prop="condition" width="180"/>
                        <el-table-column prop="value" width="180"/>
                        <el-table-column>
                            <template #default="scope">
                                <el-button
                                        link
                                        @click.prevent="deleteRow(scope.$index)"
                                        :icon="CloseBold"
                                        style="border-color: black"
                                />
                            </template>
                        </el-table-column>
                    </el-table>
                </div>
                <div class="submitButton" v-show="isVisualisationSelected">
                    <el-button size="" @click="addFilterVisible = true" :icon="Plus">
                        Add filter condition
                    </el-button>
                    <el-dialog v-model="addFilterVisible" title="Add filter condition" style="width: 500px">
                        <el-form
                                ref="filterRuleFormRef"
                                :model="filterForm"
                                :rules="filterRules"
                                label-width="150px"
                                class="demo-ruleForm"
                                status-icon
                        >
                            <el-form-item label="Choice" prop="choice">
                                <el-select v-model="filterForm.choice"
                                           placeholder="Please select attributes or schema patterns">
                                    <el-option label="attributes" value="attributes"/>
                                    <el-option label="schema patterns" value="schema patterns"/>
                                </el-select>
                            </el-form-item>

                            <el-form-item label="Relationship" prop="relationship"
                                          v-show="filterForm.choice==='schema patterns'">
                                <el-select v-model="filterForm.relationship" placeholder="Please select relationship"
                                           @change="chooseFilterRS">
                                    <div v-for="(rs, index) in basicEntityRelationShips" :key="index">
                                        <el-option :label="rs.relation1 + '(basic-entity)'"
                                                   :value="rs.relation1+'1'"></el-option>
                                    </div>
                                    <div v-for="(rs, index) in weakEntityRelationShips" :key="index">
                                        <el-option :label="rs.relation1 + '(weak-entity)'"
                                                   :value="rs.relation1+'2'"></el-option>
                                    </div>
                                    <div v-for="(rs, index) in oneManyRelationShips" :key="index">
                                        <el-option :label="rs.relation1 + '(one-many)'"
                                                   :value="rs.relation1+'3'"></el-option>
                                    </div>
                                    <div v-for="(rs, index) in manyManyRelationShips" :key="index">
                                        <el-option :label="rs.relation1 + '(many-many)'"
                                                   :value="rs.relation1+'4'"></el-option>
                                    </div>
                                </el-select>
                            </el-form-item>

                            <el-form-item label="Attribute" prop="attribute">
                                <el-select v-model="filterForm.attribute"
                                           placeholder="Please select attributes or schema patterns">
                                    <div v-for="(value, index) in allAttributesPK" :key="index"
                                         v-show="filterForm.choice==='attributes'">
                                        <div>
                                            <el-option :label="value" :value="value"/>
                                        </div>
                                    </div>
                                    <div v-for="(value, index) in otherAttributesPK" :key="index"
                                         v-show="filterForm.choice==='schema patterns'">
                                        <div>
                                            <el-option :label="value" :value="value"/>
                                        </div>
                                    </div>
                                </el-select>
                            </el-form-item>
                            <el-form-item label="Condition" prop="condition">
                                <el-select v-model="filterForm.condition" placeholder="Please select a condition">
                                    <el-option label="(number) greater than" value=">="/>
                                    <el-option label="(number) greater than or equal" value=">"/>
                                    <el-option label="(number) less than" value="<"/>
                                    <el-option label="(number) less than or equal" value="<="/>
                                    <el-option label="(text) equals" value="="/>
                                    <el-option label="(text) does not equal" value="!="/>
                                    <el-option label="(text) contains" value="contains"/>
                                    <el-option label="(text) does not contain" value="does not contain"/>
                                    <el-option label="(text) starts with" value="starts with"/>
                                    <el-option label="(text) ends with" value="ends with"/>
                                </el-select>
                            </el-form-item>
                            <el-form-item label="Value" prop="value">
                                <el-input v-model="filterForm.value" style="width: 250px"/>
                            </el-form-item>
                            <el-form-item>
                                <el-button type="info" @click="addFilterVisible = false" :icon="Close">Cancel
                                </el-button>
                                <el-button type="primary" color="green" @click="addFilter(filterRuleFormRef)"
                                           :icon="Check">Confirm
                                </el-button>
                            </el-form-item>
                        </el-form>
                    </el-dialog>
                </div>
                <div class="selections" v-show="isSorterAdded">
                    <h1>Sorter and number limit: </h1>
                    <div class="sort">
                        <p1 style="font-size: 14px; color: rgb(96, 98, 102); margin-right: 10px"> Sort by :</p1>
                        <el-button disabled>{{sortKey}}</el-button>
                    </div>
                    <div class="sort">
                        <p1 style="font-size: 14px; color: rgb(96, 98, 102); margin-right: 10px"> Order :</p1>
                        <el-button disabled>{{order}}</el-button>
                    </div>
                    <div class="sort" style="border-bottom: 2px solid rgb(235, 238, 245);">
                        <p1 style="font-size: 14px; color: rgb(96, 98, 102); margin-right: 10px"> Number limit :</p1>
                        <el-button disabled>{{limit}}</el-button>
                    </div>
                </div>
                <div class="submitButton" v-show="isVisualisationSelected">
                    <el-button @click="addSorterVisible = true" :icon="Edit">
                        Set sorter and number limit
                    </el-button>
                    <el-dialog v-model="addSorterVisible" title="Set sorter and number limit" style="width: 500px">
                        <el-form
                                ref="sorterRuleFormRef"
                                :model="sorterForm"
                                :rules="sorterRules"
                                label-width="150px"
                                class="demo-ruleForm"
                                status-icon
                        >
                            <el-form-item label="Sort by" prop="sortKey">
                                <el-select v-model="sorterForm.sortKey" placeholder="Please select an attribute">
                                    <div v-for="(value, index) in allAttributesPK" :key="index">
                                        <div>
                                            <el-option :label="value" :value="value"/>
                                        </div>
                                    </div>
                                </el-select>
                            </el-form-item>
                            <el-form-item label="Order" prop="order">
                                <el-select v-model="sorterForm.order" placeholder="Please select an order">
                                    <el-option label="ascending" value="ascending"/>
                                    <el-option label="descending" value="descending"/>
                                </el-select>
                            </el-form-item>
                            <el-form-item label="Value" prop="limit">
                                <el-input v-model="sorterForm.limit" style="width: 250px"/>
                            </el-form-item>
                            <el-form-item>
                                <el-button type="info" @click="addSorterVisible = false" :icon="Close">Cancel
                                </el-button>
                                <el-button type="success" color="green" @click="addSorter(sorterRuleFormRef)"
                                           :icon="Check">Confirm
                                </el-button>
                            </el-form-item>
                        </el-form>
                    </el-dialog>
                </div>
                <div class="selections" v-show="isIdentifierAdded">
                    <h1>Identified attribute: </h1>
                    <div class="sort" style="border-bottom: 2px solid rgb(235, 238, 245);">
                        <p1 style="font-size: 14px; color: rgb(96, 98, 102); margin-right: 10px"> Identified by :</p1>
                        <el-button disabled>{{identifier}}</el-button>
                    </div>
                </div>
                <div class="submitButton" v-show="isVisualisationSelected&&choice==='attributes'">
                    <el-button @click="chooseIdentifier" :icon="Edit">
                        Set identified attribute
                    </el-button>
                    <el-dialog v-model="addIdentifierVisible" title="Set identified attribute" style="width: 500px">
                        <el-form
                                ref="identifierRuleFormRef"
                                :model="identifierForm"
                                :rules="identifierRules"
                                label-width="150px"
                                class="demo-ruleForm"
                                status-icon
                        >
                            <el-form-item label="Identified by" prop="identifier">
                                <el-select v-model="identifierForm.identifier" placeholder="Please select an attribute">
                                    <div v-for="(value, index) in primaryKeys" :key="index">
                                        <div>
                                            <el-option :label="value+'(primary key)'" :value="value"/>
                                        </div>
                                    </div>
                                    <div v-for="(value, index) in uniqueAttrs" :key="index">
                                        <div>
                                            <el-option :label="value+'(unique)'" :value="value"/>
                                        </div>
                                    </div>
                                    <div v-for="(value, index) in otherAttrs" :key="index">
                                        <div>
                                            <el-option :label="value" :value="value"/>
                                        </div>
                                    </div>
                                </el-select>
                            </el-form-item>
                            <el-form-item>
                                <el-button type="info" @click="addIdentifierVisible = false" :icon="Close">Cancel
                                </el-button>
                                <el-button type="success" color="green" @click="addIdentifier(identifierRuleFormRef)"
                                           :icon="Check">Confirm
                                </el-button>
                            </el-form-item>
                        </el-form>
                    </el-dialog>
                </div>
                <div class="submitButton" v-show="isVisualisationSelected">
                    <el-button type="primary" @click="generateGraph" color="green" size="large" :icon="Histogram">
                        Generate Graph
                    </el-button>
                </div>
            </el-main>
        </el-container>
    </div>
</template>


<script lang="ts" setup>
    import {reactive, ref} from 'vue'
    import axios from "axios";
    import {Action, ElMessage, ElMessageBox, FormInstance, FormRules} from "element-plus";
    import {useRouter} from "vue-router";
    import {Search, Check, Close, CloseBold, Histogram, Edit, Plus} from '@element-plus/icons-vue'

    const router = useRouter()

    const allRelations = ref([])
    const selectedRelation = ref()

    const isRelationSelected = ref(false)
    const relationData = ref([])

    const choice = ref('')

    //Only attributes
    const thisAttributes = ref([])
    const allAttributesPK = ref([]);
    const isAttributesSelected = ref(false)

    //Schema patterns
    const basicEntityRelationShips = ref([]);
    const weakEntityRelationShips = ref([]);
    const oneManyRelationShips = ref([]);
    const manyManyRelationShips = ref([]);

    const isRelationShipSelected = ref(false)
    const selectedRelationShip = ref()
    let anotherEntity = ""
    let relationshipType = ""

    const otherAttributes = ref([]);
    const otherAttributesPK = ref([]);

    //Final selected attributes
    const selectedAttributes = ref([])

    const request = {}

    const recommendTypes = ref([])
    const isUniqueAttrs = ref([])
    const isRecommendationFinished = ref(false)
    const selectedVisualisation = ref()

    const isVisualisationSelected = ref(false)

    const created = () => {
        axios.get("http://localhost:8182/graph/init").then(function (resp) {
            allRelations.value = resp.data
        })
    }
    created();

    const chooseRelation = () => {
        clear(0)

        isRelationSelected.value = true
        relationData.value = allRelations.value[selectedRelation.value]
        request["relation"] = selectedRelation.value

        //Only attributes
        thisAttributes.value = relationData.value["nonKeys"]
        allAttributesPK.value = relationData.value["primaryKeys"].concat(relationData.value["nonKeys"])

        //Schema patterns
        basicEntityRelationShips.value = [];
        weakEntityRelationShips.value = [];
        oneManyRelationShips.value = [];
        manyManyRelationShips.value = [];
        if (relationData.value["basicEntityRelationShips"] != null) {
            for (const index in relationData.value["basicEntityRelationShips"]) {
                if (Number(index) == 0 || relationData.value["basicEntityRelationShips"][Number(index) - 1]["relation1"] != relationData.value["basicEntityRelationShips"][index]["relation1"]) {
                    basicEntityRelationShips.value.push(relationData.value["basicEntityRelationShips"][index])
                }
            }
        }
        if (relationData.value["weakEntityRelationShips"] != null) {
            for (const index in relationData.value["weakEntityRelationShips"]) {
                if (Number(index) == 0 || relationData.value["weakEntityRelationShips"][Number(index) - 1]["relation1"] != relationData.value["weakEntityRelationShips"][index]["relation1"]) {
                    weakEntityRelationShips.value.push(relationData.value["weakEntityRelationShips"][index])
                }
            }
        }
        if (relationData.value["oneManyRelationShips"] != null) {
            for (const index in relationData.value["oneManyRelationShips"]) {
                if (Number(index) == 0 || relationData.value["oneManyRelationShips"][Number(index) - 1]["relation1"] != relationData.value["oneManyRelationShips"][index]["relation1"]) {
                    oneManyRelationShips.value.push(relationData.value["oneManyRelationShips"][index])
                }
            }
        }
        if (relationData.value["manyManyRelationShips"] != null) {
            for (const index in relationData.value["manyManyRelationShips"]) {
                if (Number(index) == 0 || relationData.value["manyManyRelationShips"][Number(index) - 1]["relation1"] != relationData.value["manyManyRelationShips"][index]["relation1"]) {
                    manyManyRelationShips.value.push(relationData.value["manyManyRelationShips"][index])
                }
            }
        }
    }

    const choose = () => {
        clear(1)
        allAttributesPK.value = relationData.value["primaryKeys"].concat(relationData.value["nonKeys"])
    }

    //Only attributes
    const chooseAttributes = () => {
        clear(3)

        isAttributesSelected.value = true
        if (choice.value == "attributes") {
            request["type"] = "basic-entity"
            request["anotherRelation"] = selectedRelation.value
        }
        request["attributes"] = selectedAttributes.value
    }

    const chooseRelationship = () => {
        clear(2)

        isRelationShipSelected.value = true
        const type = selectedRelationShip.value.slice(-1)
        if (type == "1") {
            relationshipType = "basic-entity"
        } else if (type == "2") {
            relationshipType = "weak-entity"
        } else if (type == "3") {
            relationshipType = "one-many"
        } else if (type == "4") {
            relationshipType = "many-many"
        }
        request["type"] = relationshipType
        anotherEntity = selectedRelationShip.value.substr(0, selectedRelationShip.value.length - 1)
        if (type == "1" || type == "2" || type == "3") {
            //Basic Entity or Weak Entity or Many-Many Relationship
            otherAttributes.value = allRelations.value[anotherEntity]["nonKeys"]
            allAttributesPK.value = allRelations.value[anotherEntity]["primaryKeys"].concat(allRelations.value[anotherEntity]["nonKeys"])
            request["anotherRelation"] = anotherEntity
            request["junctionRelation"] = ''
        } else {
            //Many-Many Relationship
            for (const index in manyManyRelationShips.value) {
                if (manyManyRelationShips.value[index]["relation1"] == anotherEntity) {
                    const rs = manyManyRelationShips.value[index]
                    otherAttributes.value = allRelations.value[rs["junctionRelation"]]["nonKeys"]
                    allAttributesPK.value = allRelations.value[rs["junctionRelation"]]["primaryKeys"].concat(allRelations.value[rs["junctionRelation"]]["nonKeys"])
                    request["anotherRelation"] = anotherEntity
                    request["junctionRelation"] = rs["junctionRelation"]
                    break
                }
            }
        }
    }

    const recommend = async (undefined) => {
        axios.put("http://localhost:8182/graph/recommend", request).then(function (resp) {
            if (typeof resp.data != "string") {
                recommendTypes.value = resp.data["recommendTypes"]
                isUniqueAttrs.value = resp.data["isUniqueAttrs"]
                if (recommendTypes.value.length > 0) {
                    isRecommendationFinished.value = true
                    ElMessageBox.confirm(
                        'Found the recommended visualisation types successfully!',
                        'Success',
                        {
                            confirmButtonText: 'OK',
                            cancelButtonText: 'Cancel',
                        }
                    ).then(() => {
                    }).catch(() => {
                    })
                } else {
                    ElMessageBox.confirm(
                        'There are no suitable visualisation types, please try to choose other attributes or relationships',
                        'Sorry',
                        {
                            confirmButtonText: 'OK',
                            cancelButtonText: 'Cancel',
                        }
                    ).then(() => {
                    }).catch(() => {
                    })
                }
            } else {
                console.log(resp.data)
                ElMessageBox.alert(resp.data, 'Error', {
                    // if you want to disable its autofocus
                    // autofocus: false,
                    confirmButtonText: 'OK',
                    callback: (action: Action) => {
                        ElMessage({
                            type: 'info',
                            message: `action: ${action}`,
                        })
                    },
                })
            }
        })
    }

    const chooseVisualisation = () => {
        clear(4)
        isVisualisationSelected.value = true
    }

    const addFilterVisible = ref(false)

    interface FilterForm {
        choice: string;
        relationship: string;
        junctionRelation: string;
        type: string;
        attribute: string;
        condition: string;
        value: string;
    }

    const filterRuleFormRef = ref<FormInstance>()
    const filterForm = reactive<FilterForm>({
        choice: 'attributes',
        relationship: '',
        junctionRelation: '',
        type: '',
        attribute: '',
        condition: '',
        value: '',
    })
    const filterRules = reactive<FormRules<FilterForm>>({
        choice: [
            // { required: true, message: 'Please choose an attribute', trigger: 'change' },
        ],
        relationship: [],
        attribute: [],
        condition: [],
        value: [
            {required: true, message: 'Please input value', trigger: 'blur'},
        ]
    })

    const chooseFilterRS = () => {
        const type = filterForm['relationship'].slice(-1)
        if (type == "1") {
            filterForm['type'] = "basic-entity"
        } else if (type == "2") {
            filterForm['type'] = "weak-entity"
        } else if (type == "3") {
            filterForm['type'] = "one-many"
        } else if (type == "4") {
            filterForm['type'] = "many-many"
        }
        filterForm['relationship'] = filterForm['relationship'].substr(0, filterForm['relationship'].length - 1)
        if (type == "1" || type == "2" || type == "3") {
            //Basic Entity or Weak Entity or Many-Many Relationship
            otherAttributesPK.value = allRelations.value[filterForm['relationship']]["primaryKeys"].concat(allRelations.value[filterForm['relationship']]["nonKeys"])
            filterForm['junctionRelation'] = ''
        } else {
            //Many-Many Relationship
            for (const index in manyManyRelationShips.value) {
                if (manyManyRelationShips.value[index]["relation1"] == filterForm['relationship']) {
                    const rs = manyManyRelationShips.value[index]
                    otherAttributesPK.value = allRelations.value[rs["junctionRelation"]]["primaryKeys"].concat(allRelations.value[rs["junctionRelation"]]["nonKeys"])
                    filterForm['junctionRelation'] = rs["junctionRelation"]
                    break
                }
            }
        }
    }

    const conditions = ref([])
    const addFilter = async (formEl: FormInstance | undefined) => {
        if (!formEl) return
        await formEl.validate((valid, fields) => {
            if (valid) {
                const condition = {}
                condition['choice'] = filterForm['choice']
                condition['anotherRelation'] = filterForm['relationship']
                if (filterForm['choice'] == 'attributes') {
                    condition['type'] = 'basic-entity'
                    condition['junctionRelation'] = ''
                } else {
                    condition['type'] = filterForm['type']
                    condition['junctionRelation'] = filterForm['junctionRelation']
                }
                condition['attribute'] = filterForm['attribute']
                condition['condition'] = filterForm['condition']
                condition['value'] = filterForm['value']
                conditions.value.push(condition)
                addFilterVisible.value = false
                formEl.resetFields()
            } else {
                console.log('error submit!', fields)
            }
        })
    }

    const deleteRow = (index: number) => {
        conditions.value.splice(index, 1)
    }

    const addSorterVisible = ref(false)
    const isSorterAdded = ref(false)

    interface SorterForm {
        sortKey: string;
        order: string;
        limit: string;
    }

    const sorterRuleFormRef = ref<FormInstance>()
    const sorterForm = reactive<SorterForm>({
        sortKey: 'default',
        order: 'default',
        limit: '1000'
    })
    const sorterRules = reactive<FormRules<SorterForm>>({
        sortKey: [
            // { required: true, message: 'Please choose an attribute', trigger: 'change' },
        ],
        order: [
            // { required: true, message: 'Please choose an order', trigger: 'change' },
        ],
        limit: [
            {required: true, message: 'Please input number limit', trigger: 'blur'},
        ]
    })

    const sortKey = ref()
    const order = ref()
    const limit = ref()
    const addSorter = async (formEl: FormInstance | undefined) => {
        if (!formEl) return
        await formEl.validate((valid, fields) => {
            if (valid) {
                const condition = {}
                sortKey.value = sorterForm['sortKey']
                order.value = sorterForm['order']
                limit.value = sorterForm['limit']
                addSorterVisible.value = false
                isSorterAdded.value = true
            } else {
                console.log('error submit!', fields)
            }
        })
    }

    const isInitIdentifiers = ref(false)
    const primaryKeys = ref([])
    const uniqueAttrs = ref([])
    const otherAttrs = ref([])
    const chooseIdentifier = () => {
        primaryKeys.value = []
        uniqueAttrs.value = []
        otherAttrs.value = []
        addIdentifierVisible.value = true
        for (const key in isUniqueAttrs.value) {
            if (isUniqueAttrs.value[key] == "primary key") {
                primaryKeys.value.push(key)
            } else if (isUniqueAttrs.value[key] == "unique") {
                uniqueAttrs.value.push(key)
            } else {
                otherAttrs.value.push(key)
            }
        }
    }
    const addIdentifierVisible = ref(false)
    const isIdentifierAdded = ref(false)

    interface IdentifierForm {
        identifier: string;
    }

    const identifierRuleFormRef = ref<FormInstance>()
    const identifierForm = reactive<IdentifierForm>({
        identifier: 'default'
    })
    const identifierRules = reactive<FormRules<SorterForm>>({})

    const identifier = ref()
    const addIdentifier = async (formEl: FormInstance | undefined) => {
        if (!formEl) return
        await formEl.validate((valid, fields) => {
            if (valid) {
                identifier.value = identifierForm['identifier']
                addIdentifierVisible.value = false
                isIdentifierAdded.value = true
            } else {
                console.log('error submit!', fields)
            }
        })
    }

    const generateGraph = () => {
        try {
            const newpage = router.resolve({
                name: selectedVisualisation.value.toLowerCase(),
                query: {
                    choice: choice.value,
                    type: request['type'],
                    relation: request['relation'],
                    anotherRelation: request['anotherRelation'],
                    attributes: request['attributes'],
                    junctionRelation: request['junctionRelation'],
                    vsType: selectedVisualisation.value,
                    conditions: JSON.stringify(conditions.value),
                    sortKey: sortKey.value,
                    order: order.value,
                    limit: limit.value,
                    identifier: identifier.value
                }
            })
            window.open(newpage.href, '_blank')
        } catch (e) {
            console.log(e)
            ElMessageBox.alert("This visualisation type is till developing, please choose another visualisation type!",
                'Error', {
                    // if you want to disable its autofocus
                    // autofocus: false,
                    confirmButtonText: 'OK',
                    callback: (action: Action) => {
                        ElMessage({
                            type: 'info',
                            message: `action: ${action}`,
                        })
                    },
                })
        }
    }

    const clear = (level) => {
        //choose relation
        if (1 > level) {
            choice.value = ''
        }
        //choose attr or schema patterns
        if (2 > level) {
            isAttributesSelected.value = false
            isRelationShipSelected.value = false
            selectedRelationShip.value = []
        }
        //choose schema pattern
        if (3 > level) {
            selectedAttributes.value = []
            otherAttributes.value = []
            if (filterRuleFormRef.value) filterRuleFormRef.value.resetFields()
            if (sorterRuleFormRef.value) sorterRuleFormRef.value.resetFields()
            if (identifierRuleFormRef.value) identifierRuleFormRef.value.resetFields()
        }
        //choose attrubutes
        if (4 > level) {
            isRecommendationFinished.value = false
            isVisualisationSelected.value = false
            selectedVisualisation.value = ''
            recommendTypes.value = []
        }
        //choose visualisation
        if (5 > level) {
            isSorterAdded.value = false
            isIdentifierAdded.value = false

            conditions.value = []
            sortKey.value = ''
            order.value = ''
            limit.value = ''
            identifier.value = ''

            primaryKeys.value = []
            uniqueAttrs.value = []
            otherAttrs.value = []
        }
    }

</script>

<style scoped>
    .el-main {
        display: flex;
        flex-direction: column;
        padding: 0;
    }

    .selections {
        padding: 5px;
    }

    .el-radio {
        margin-left: 10px;
        margin-bottom: 5px;
        margin-right: 0px;
    }

    .el-checkbox {
        margin-left: 10px;
        margin-bottom: 5px;
    }

    .submitButton {
        margin-top: 10px;
        margin-bottom: 10px;
        margin-left: 10px;
    }

    .sort {
        width: 50%;
        margin-left: 10px;
        padding: 6px;
        border-top: 2px solid rgb(235, 238, 245);
    }
</style>
