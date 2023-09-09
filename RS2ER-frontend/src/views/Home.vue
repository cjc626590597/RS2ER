<template>
    <div class="common-layout">
        <el-container>
            <el-header style="height: 45px">
                <el-menu>
                    <div class="header">
                        <img class="logo" alt="ERD logo" src="../assets/logo1.jpg">
                        <div>
                            <p1 class="title">SCHEMA ANALYSIS</p1>
                        </div>
                    </div>
                </el-menu>
            </el-header>
            <el-header>
                <el-menu router
                         :default-active="this.$router.path"
                         class="menu-container"
                         mode="horizontal"
                         @select="handleSelect">
                    <el-menu-item class="menu-item-left" index="/table">
                        <el-icon>
                            <Menu/>
                        </el-icon>
                        TABLES
                    </el-menu-item>
                    <el-menu-item class="menu-item-left" index="/erd">
                        <el-icon>
                            <CollectionTag/>
                        </el-icon>
                        ERD
                    </el-menu-item>
                    <el-menu-item class="menu-item-left" index="/graph">
                        <el-icon>
                            <TrendCharts/>
                        </el-icon>
                        GRAPHS
                    </el-menu-item>
                    <el-menu-item class="menu-item-right" index="/" style="">
                        <el-icon>
                            <CaretLeft/>
                        </el-icon>
                        EXIT
                    </el-menu-item>
                </el-menu>
            </el-header>
            <el-main class="centered-content">
                <router-view></router-view>
            </el-main>
        </el-container>
    </div>
</template>

<script lang="ts" setup>
    import {ref} from 'vue'
    import {Menu, CollectionTag, TrendCharts, CaretLeft} from '@element-plus/icons-vue'

    const handleSelect = (key: string, keyPath: string) => {
        // console.log(key, keyPath)
    }
</script>

<style scoped>
    .common-layout {
        display: flex;
        justify-content: center;
        align-items: center;
    }

    .header {
        margin: 0;
        display: flex;
    }

    .logo {
        width: 40px;
        height: 40px;
        margin-top: 5px;
        margin-left: 20px;
        border-radius: 50%;
    }

    .title {
        position: absolute;
        left: 80px;
        top: 10px;
        color: #f3ecec;
        font-size: 25px;
    }

    .menu-container {
        display: flex;
        justify-content: space-between;
    }

    .el-menu {
        background: rgb(35, 123, 255);
    }

    .el-menu-item {
        height: 40px;
        color: white;
    }

    .menu-item-left {
        width: 300px;
    }

    .menu-item-right {
        width: 100px;
        margin-left: auto;
    }

    .centered-content {
        padding-top: 0;
        padding-left: 40px;
        padding-right: 40px;
    }
</style>