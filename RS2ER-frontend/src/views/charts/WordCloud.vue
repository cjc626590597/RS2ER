<template>
    <el-container>
        <div>
            <div class="title">
                <p>{{title}}</p>
            </div>
            <div class="chart" ref="chart"
                 style="width: 1200px; height: 600px">
            </div>
        </div>
    </el-container>
</template>


<script lang="ts" setup>
    import {useRoute} from "vue-router"//

    import {ref, onMounted} from "vue";
    import * as echarts from "echarts";
    import 'echarts-wordcloud';
    import axios from "axios";
    import {Action, ElMessage, ElMessageBox} from "element-plus";

    const route = useRoute()
    const chart = ref()
    const title = ref()

    onMounted(
        () => {
            generate()
        }
    )

    // Generate the chart
    function generate() {
        var myChart = echarts.init(chart.value);
        if (typeof route.query['attributes'] == "string") {
            route.query['attributes'] = [route.query['attributes']]
        }
        axios.put("http://localhost:8182/graph/chart", route.query).then(function (resp) {
            if (typeof resp.data != "string") {
                const maskImage = new Image();
                maskImage.src = 'data:image/png;base64,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'
                let pksName = resp.data["pksName"]
                let jsonData = resp.data["jsonData"]
                let attr = route.query['attributes'][0]
                if (route.query['choice'] == "attributes") {
                    title.value = route.query['vsType'] + ' -- ' + 'Entity ' + route.query['relation'] + ' for attributes (' + route.query['attributes'] + ')'
                } else {
                    if (route.query['type'] == 'one-many' || route.query['type'] == 'many-many') {
                        title.value = route.query['vsType'] + ' -- ' + 'Entity ' + route.query['relation'] + " \'s " + route.query['type'] + ' relationship: ' + route.query['anotherRelation'] + ' for attributes (' + route.query['attributes'] + ')'
                    } else {
                        title.value = route.query['vsType'] + ' -- ' + 'Entity ' + route.query['relation'] + " \'s " + route.query['type'] + ': ' + route.query['anotherRelation'] + ' for attributes (' + route.query['attributes'] + ')'
                    }
                }
                let dataset = [];
                for (const index in jsonData) {
                    const data = {}
                    if (typeof jsonData[index][attr] != 'number') {
                        data['value'] = 0
                    } else {
                        data['value'] = jsonData[index][attr]
                    }
                    data['name'] = jsonData[index][pksName]
                    dataset.push(data)
                }
                dataset.sort((a, b) => {
                    return a['value'] < b['value'] ? 1 : -1;
                });
                var option = {
                    tooltip: {
                        formatter: function (params) {
                            return (
                                pksName + ': ' + params.data['name'] + ' ' +
                                attr + ' : ' + params.data['value']
                            );
                        }
                    },
                    legend: {},
                    series: [{
                        type: 'wordCloud',
                        shape: 'circle',
                        keepAspect: false,
                        // maskImage: maskImage,
                        left: 'center',
                        top: 'center',
                        width: '100%',
                        height: '100%',
                        right: null,
                        bottom: null,
                        sizeRange: [12, 60],
                        rotationRange: [-90, 90],
                        rotationStep: 45,
                        gridSize: 8,
                        drawOutOfBound: false,
                        shrinkToFit: false,
                        layoutAnimation: true,
                        textStyle: {
                            fontFamily: 'sans-serif',
                            fontWeight: 'bold',
                            color: function () {
                                return 'rgb(' + [
                                    Math.round(Math.random() * 160),
                                    Math.round(Math.random() * 160),
                                    Math.round(Math.random() * 160)
                                ].join(',') + ')';
                            }
                        },
                        emphasis: {
                            focus: 'self',
                            textStyle: {
                                textShadowBlur: 10,
                                textShadowColor: '#333'
                            }
                        },
                        data: dataset.slice(0, 100)
                    }]
                };
                maskImage.onload = function () {
                    myChart.setOption(option);
                };
            } else {
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
</script>

<style scoped>
    .title {
        display: flex;
        justify-content: center;
        font-size: 20px;
        font-weight: bold;
    }

    .chart {
        display: flex;
        justify-content: center;
        width: 1200px;
        height: 600px;
    }
</style>