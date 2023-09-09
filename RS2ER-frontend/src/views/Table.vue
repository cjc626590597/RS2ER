<template>
    <div class="common-layout">
        <el-container>
            <el-main>
                <el-table :data="tableData" stripe style="width: 100%">
                    <el-table-column prop="name" label="Name" width="180"/>
                    <el-table-column prop="type" label="Type" width="180"/>
                    <el-table-column prop="primaryKeys" label="Primary keys" class="cell">
                        <template #default="scope">
                            <div v-for="(primaryKey, index) in scope.row.primaryKeys" :key="index">
                                {{ primaryKey }}
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="nonKeys" label="Non-PK attributes" class="cell">
                        <template #default="scope">
                            <div v-for="(nonKey, index) in scope.row.nonKeys" :key="index">
                                {{ nonKey }}
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="rows" label="Number of records" width="180"/>
                    <el-table-column label="Relation diagrams" width="300">
                        <template #default="scope">
                            <div style="display: flex; align-items: center">
                                <el-image class="tableImage"
                                          :src="require('D:/IdeaProjects/RS2ER-backend/path/to/output/diagrams/tables/' + scope.row.name + '.1degree.png')"
                                          @click="showImage(require('D:/IdeaProjects/RS2ER-backend/path/to/output/diagrams/tables/' + scope.row.name + '.1degree.png'))"
                                />
                            </div>
                        </template>
                    </el-table-column>
                </el-table>
                <el-image-viewer
                        v-if="showViewer"
                        @close="closeViewer"
                        :url-list="srcList">
                </el-image-viewer>
            </el-main>
        </el-container>
    </div>
</template>

<script lang="ts" setup>
    import {ref} from 'vue'
    import axios from "axios";

    const tableData = ref([])

    const srcList = ref([])
    const showViewer = ref(false)

    const created = () => {
        axios.get("http://localhost:8182/erd/table").then(function (resp) {
            tableData.value = resp.data
        })
    }
    created();

    const showImage = (path) => {
        let list = []
        list.push(path)
        srcList.value = list
        showViewer.value = true
        stopScroll()
    }

    const closeViewer = () => {
        showViewer.value = false
        canScroll()
    }

    const stopScroll = () => {
        var mo = function (e) {
            e.preventDefault();
        };
        document.body.style.overflow = 'hidden';
        document.addEventListener("touchmove", mo, false);
    }

    const canScroll = () => {
        var mo = function (e) {
            e.preventDefault();
        };
        document.body.style.overflow = '';
        document.removeEventListener("touchmove", mo, false);
    }

</script>

<style scoped>
    .el-table .cells {
    }

    .el-image {
        cursor: pointer;
    }
</style>