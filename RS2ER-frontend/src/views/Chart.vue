<template>
    <el-container>
        <el-main class="canvasImgObj">
            <div style="margin-top: 15px">
                <router-view></router-view>
            </div>
        </el-main>
        <div class="imageButton">
            <el-button @click="GenerateImg" type="primary" color="green" :icon="PictureFilled">Save Image</el-button>
        </div>
    </el-container>
</template>


<script lang="ts" setup>
    import html2canvas from "html2canvas"
    import {PictureFilled} from '@element-plus/icons-vue'
    import {onMounted} from "vue";
    import {useRoute} from "vue-router";

    const route = useRoute()
    onMounted(
        () => {
            start()
        }
    )

    let fileName = ""

    function start() {
        if (route.query['choice'] == "attributes") {
            fileName = route.query['vsType'] + '__' + 'Entity_' + route.query['relation'] + '_for_attributes_(' + route.query['attributes'] + ')'
        } else {
            if (route.query['type'] == 'one-many' || route.query['type'] == 'many-many') {
                fileName = route.query['vsType'] + '__' + 'Entity_' + route.query['relation'] + "\'s_" + route.query['type'] + '_relationship: ' + route.query['anotherRelation'] + '_for_attributes_(' + route.query['attributes'] + ')'
            } else {
                fileName = route.query['vsType'] + '__' + 'Entity_' + route.query['relation'] + "\'s_" + route.query['type'] + ': ' + route.query['anotherRelation'] + '_for_attributes_(' + route.query['attributes'] + ')'
            }
        }
        console.log(fileName)
    }

    function GenerateImg() {
        let canvas = document.querySelector('.canvasImgObj');
        html2canvas(canvas, {scale: 2, logging: false, useCORS: true}).then(function (canvas) {
            let type = 'png';
            let imgData = canvas.toDataURL(type);
            let _fixType = function (type) {
                type = type.toLowerCase().replace(/jpg/i, 'jpeg');
                let r = type.match(/png|jpeg|bmp|gif/)[0];
                return 'image/' + r;
            };
            imgData = imgData.replace(_fixType(type), 'image/octet-stream');
            let filename = fileName + '.' + type;
            saveFile(imgData, filename);
        });
    }

    const saveFile = (data, filename) => {
        let save_link = document.createElementNS('http://www.w3.org/1999/xhtml', 'a');
        save_link.href = data;
        save_link.download = filename;
        let event = document.createEvent('MouseEvents');
        event.initMouseEvent('click', true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
        save_link.dispatchEvent(event);
    }
</script>

<style scoped>
    .imageButton {
        position: absolute;
        left: 1250px;
        top: 600px;
    }
</style>