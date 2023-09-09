<template>
    <el-container>
        <div>
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
    import axios from "axios";
    import {Action, ElMessage, ElMessageBox} from "element-plus";

    const route = useRoute()
    const chart = ref()

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
                let pksName = resp.data["pksName"]
                let jsonData = resp.data["jsonData"]
                let attr0 = route.query['attributes'][0]
                let attr1 = route.query['attributes'][1]
                let attr2 = route.query['attributes'][2]
                let title = ""
                if (route.query['choice'] == "attributes") {
                    title = route.query['vsType'] + ' -- ' + 'Entity ' + route.query['relation'] + ' for attributes (' + route.query['attributes'] + ')'
                } else {
                    if (route.query['type'] == 'one-many' || route.query['type'] == 'many-many') {
                        title = route.query['vsType'] + ' -- ' + 'Entity ' + route.query['relation'] + " \'s " + route.query['type'] + ' relationship: ' + route.query['anotherRelation'] + ' for attributes (' + route.query['attributes'] + ')'
                    } else {
                        title = route.query['vsType'] + ' -- ' + 'Entity ' + route.query['relation'] + " \'s " + route.query['type'] + ': ' + route.query['anotherRelation'] + ' for attributes (' + route.query['attributes'] + ')'
                    }
                }
                let dataset = [];
                let maxAttr2 = Number.NEGATIVE_INFINITY
                let minAttr2 = Number.MAX_SAFE_INTEGER
                for (const index in jsonData) {
                    const data = []
                    data.push(jsonData[index][attr0])
                    data.push(jsonData[index][attr1])
                    data.push(jsonData[index][attr2])
                    if (jsonData[index][attr2] > maxAttr2) {
                        maxAttr2 = jsonData[index][attr2]
                    }
                    if (jsonData[index][attr2] < minAttr2) {
                        minAttr2 = jsonData[index][attr2]
                    }
                    data.push(jsonData[index][pksName])
                    dataset.push(data)
                }
                console.log(dataset)
                var option = {
                    title: {
                        text: title,
                        left: 'center'
                    },
                    tooltip: {
                        formatter: function (params) {
                            return (
                                pksName + ': ' + params.data[3] + ' ' +
                                attr0 + ': ' + params.data[0] + ' ' +
                                attr1 + ': ' + params.data[1] + ' ' +
                                attr2 + ' : ' + params.data[2]
                            );
                        }
                    },
                    xAxis: {
                        splitLine: {show: false},
                        scale: true,
                        name: attr0,
                        nameTextStyle: {
                            color: 'rgba(5,0,0,0.78)',
                            fontSize: 18,
                        },
                    },
                    yAxis: {
                        splitLine: {show: false},
                        scale: true,
                        name: attr1,
                        nameTextStyle: {
                            color: 'rgba(5,0,0,0.78)',
                            fontSize: 18,
                        },
                    },
                    grid: {
                        left: 40,
                        right: 130
                    },
                    series: [
                        {
                            data: dataset,
                            type: 'scatter',
                            symbolSize: function (data) {
                                return (data[2] - minAttr2) / (maxAttr2 - minAttr2) * 200;
                            },
                            emphasis: {
                                focus: 'self'
                            },
                            labelLayout: function () {
                                return {
                                    x: myChart.getWidth() - 100,
                                    moveOverlap: 'shiftY'
                                };
                            },
                            itemStyle: {
                                shadowBlur: 10,
                                shadowColor: 'rgba(14,13,13,0.53)',
                                shadowOffsetY: 5,
                                color: new echarts.graphic.RadialGradient(0.4, 0.3, 1, [
                                    {
                                        offset: 0,
                                        color: 'rgb(103, 183, 220)'
                                    },
                                    {
                                        offset: 1,
                                        color: 'rgb(252, 252, 252)'
                                    }
                                ])
                            }
                        }
                    ],
                };
                myChart.setOption(option);
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
    .chart {
        display: flex;
        justify-content: center;
        width: 1200px;
        height: 600px;
    }
</style>