<template>
    <div class="common-layout">
        <el-container>
            <el-main>
                <div class="demo-image">
                    <div class="block" style="width:60%">
                        <el-image :src="url"
                                  :preview-src-list="[url]"
                        />
                    </div>
                </div>
            </el-main>
        </el-container>
    </div>
</template>


<script lang="ts" setup>
    const url = require('D:\\IdeaProjects\\RS2ER-backend\\path\\to\\output\\diagrams\\summary\\relationships.real.large.png')
</script>

<style scoped>
</style>
