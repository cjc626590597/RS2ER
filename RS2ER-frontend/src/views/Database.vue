<template>
    <div class="common-layout">
        <el-container>
            <el-header>
            </el-header>
            <el-main>
                <img class="logo" alt="ERD logo" src="../assets/logo.jpg">
                <div>
                    <p1 class="title">SCHEMA ANALYSIS</p1>
                </div>
                <div class="connect"
                     v-loading.fullscreen.lock="loading"
                     element-loading-text="Connecting to the database and creating the conceptual model"
                     :element-loading-spinner="svg"
                     element-loading-svg-view-box="-10, -10, 50, 50">
                    <el-form
                            ref="ruleFormRef"
                            :model="ruleForm"
                            :rules="rules"
                            label-width="150px"
                            class="demo-ruleForm"
                            :size="formSize"
                            status-icon
                    >
                        <el-form-item label="Database Type" prop="databaseType">
                            <!--                            <el-select v-model="ruleForm.databaseType">-->
                            <!--                                <el-option-->
                            <!--                                        v-for="item in databaseOptions"-->
                            <!--                                        :key="item.value"-->
                            <!--                                        :label="item.label"-->
                            <!--                                        :value="item.value"-->
                            <!--                                />-->
                            <!--                            </el-select>-->
                            <el-select-v2
                                    v-model="ruleForm.databaseType"
                                    placeholder="Database Type"
                                    :options="databaseOptions"
                            />
                        </el-form-item>
                        <el-form-item label="Hostname" prop="hostname">
                            <el-input v-model="ruleForm.hostname"/>
                        </el-form-item>
                        <el-form-item label="Port Number" prop="portNum">
                            <el-input v-model="ruleForm.portNum"/>
                        </el-form-item>
                        <el-form-item label="Database Name" prop="databaseName">
                            <el-input v-model="ruleForm.databaseName"/>
                        </el-form-item>
                        <el-form-item label="Username" prop="userName">
                            <el-input v-model="ruleForm.userName"/>
                        </el-form-item>
                        <el-form-item label="Password" prop="password">
                            <el-input v-model="ruleForm.password" type="password"/>
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" @click="submitForm(ruleFormRef)" :icon="Promotion">Connect
                            </el-button>
                            <el-button type="info" @click="resetForm(ruleFormRef)" :icon="Refresh">Reset</el-button>
                        </el-form-item>
                    </el-form>
                </div>
            </el-main>
        </el-container>
    </div>
</template>

<script lang="ts" setup>
    import {reactive, ref} from 'vue'
    import {Action, ElMessage, ElMessageBox, FormInstance, FormRules} from 'element-plus'
    import axios from 'axios'
    import {useRouter} from 'vue-router';
    import {Promotion, Refresh} from '@element-plus/icons-vue'

    const router = useRouter();

    const loading = ref(false)

    const databaseOptions = [
        {
            value: 'MYSQL',
            label: 'MYSQL',
        },
        {
            value: 'POSTGRESQL',
            label: 'POSTGRESQL',
        },
        {
            value: 'SQLSERVER',
            label: 'SQLSERVER',
        }
    ]

    interface RuleForm {
        databaseType: string;
        hostname: string;
        portNum: string;
        databaseName: string;
        userName: string;
        password: string;
    }

    const formSize = ref('default')
    const ruleFormRef = ref<FormInstance>()
    const ruleForm = reactive<RuleForm>({
        databaseType: "POSTGRESQL",
        hostname: "localhost",
        portNum: '5432',
        databaseName: "mondial_plus",
        userName: "postgres",
        password: "a123456"
    })

    const rules = reactive<FormRules<RuleForm>>({
        databaseType: [
            {required: true, message: 'Please choose database type', trigger: 'change'},
        ],
        hostname: [
            {required: true, message: 'Please input hostname', trigger: 'blur'},
        ],
        portNum: [
            {required: true, message: 'Please input port number', trigger: 'blur'},
        ],
        databaseName: [
            {required: true, message: 'Please input database name', trigger: 'blur'},
        ],
        userName: [
            {required: true, message: 'Please input username', trigger: 'blur'},
        ],
        password: [
            {required: true, message: 'Please input password', trigger: 'blur'},
        ],
    })

    const submitForm = async (formEl: FormInstance | undefined) => {
        if (!formEl) return
        await formEl.validate((valid, fields) => {
            if (valid) {
                loading.value = true
                console.log('submit!')
                axios.post("http://localhost:8182/database/connect", ruleForm)
                    .then(function (resp) {
                        console.log(resp)
                        if (resp.data == "success") {
                            loading.value = false
                            ElMessageBox.confirm(
                                'Connect to the database successfully!',
                                'Success',
                                {
                                    confirmButtonText: 'OK',
                                    cancelButtonText: 'Cancel',
                                }
                            ).then(() => {
                                console.log("sucesss")
                                router.push("/table")
                            }).catch(() => {
                                console.log("cancel")
                            })
                        } else {
                            loading.value = false
                            console.log(resp.data)
                            ElMessageBox.alert(resp.data, 'Error', {
                                // if you want to disable its autofocus
                                // autofocus: false,
                                confirmButtonText: 'OK',
                                callback: (action: Action) => {
                                    ElMessage({
                                        type: 'error',
                                        message: `action: ${action}`,
                                    })
                                },
                            })
                        }
                    }).catch(resp => {
                    loading.value = false
                    console.log(resp)
                    ElMessageBox.alert(resp.message, 'Error', {
                        // if you want to disable its autofocus
                        // autofocus: false,
                        confirmButtonText: 'OK',
                        callback: (action: Action) => {
                            ElMessage({
                                type: 'error',
                                message: resp.message,
                            })
                        },
                    })
                })
            } else {
                console.log('error submit!', fields)
            }
        })
    }

    const resetForm = (formEl: FormInstance | undefined) => {
        if (!formEl) return
        formEl.resetFields()
    }
</script>

<style scoped>
    .layout-container-demo .el-main {
        padding: 0;
        width: 50%;
        display: inline-flex;
        align-items: center;
        justify-content: center;
        height: 100%;
        right: 20px;
    }

    .logo {
        position: absolute;
        width: 75px;
        height: 75px;
        left: 550px;
        display: flex;
        border-radius: 50%;
    }

    .title {
        position: absolute;
        left: 650px;
        top: 100px;
        display: flex;
        font-size: 25px;
        font-weight: bold;
        word-spacing: 3px;
        line-height: 2em;
        padding-bottom: 0.35em;
        color: rgba(0, 0, 0, 0.5);
    }

    .connect {
        position: absolute;
        left: 503px;
        top: 200px;
        width: 444px;
        height: 505px;
        display: flex;
    }
</style>