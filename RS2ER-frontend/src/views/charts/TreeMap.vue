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
                let parentPksName = resp.data["parentPksName"]
                let childPksName = resp.data["childPksName"]
                let jsonData = resp.data["jsonData"]
                let attr = route.query['attributes'][0]
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
                let parentPkData = {};
                for (const index in jsonData) {
                    if (jsonData[index][parentPksName] in parentPkData) {
                        const child = {}
                        child['name'] = jsonData[index][childPksName]
                        child['value'] = jsonData[index][attr]
                        parentPkData[jsonData[index][parentPksName]]['children'].push(child)
                    } else {
                        parentPkData[jsonData[index][parentPksName]] = {}
                        parentPkData[jsonData[index][parentPksName]]['name'] = jsonData[index][parentPksName]
                        parentPkData[jsonData[index][parentPksName]]['children'] = []
                        const child = {}
                        child['name'] = jsonData[index][childPksName]
                        child['value'] = jsonData[index][attr]
                        parentPkData[jsonData[index][parentPksName]]['children'].push(child)
                    }
                }
                const newParentPks = []
                for (const key in parentPkData) {
                    newParentPks.push(parentPkData[key])
                }
                var option = {
                    title: {
                        text: title,
                        left: 'center'
                    },
                    tooltip: {
                        formatter: function (info) {
                            var value = info.value;
                            var treePathInfo = info.treePathInfo;
                            var treePath = [];
                            for (var i = 1; i < treePathInfo.length; i++) {
                                treePath.push(treePathInfo[i].name);
                            }
                            return [
                                '<div class="tooltip-title">' +
                                echarts.format.encodeHTML(treePath.join('/')) +
                                '</div>',
                                attr + ': ' + echarts.format.addCommas(value)
                            ].join('');
                        }
                    },
                    series: {
                        name: parentPksName + ' -> ' + childPksName + ' -> ' + attr,
                        type: 'treemap',
                        data: newParentPks,
                        visibleMin: 300,
                        label: {
                            show: true,
                            formatter: '{b}'
                        },
                        upperLabel: {
                            show: true,
                            height: 30
                        },
                        itemStyle: {
                            borderColor: '#fff'
                        },
                        levels: getLevelOption(),
                    }

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

    function getLevelOption() {
        return [
            {
                itemStyle: {
                    borderColor: '#777',
                    borderWidth: 0,
                    gapWidth: 1
                },
                upperLabel: {
                    show: false
                }
            },
            {
                itemStyle: {
                    borderColor: '#555',
                    borderWidth: 5,
                    gapWidth: 1
                },
                emphasis: {
                    itemStyle: {
                        borderColor: '#ddd'
                    }
                }
            },
            {
                colorSaturation: [0.35, 0.5],
                itemStyle: {
                    borderWidth: 5,
                    gapWidth: 1,
                    borderColorSaturation: 0.6
                }
            }
        ]
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