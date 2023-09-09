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
                let firstPKsName = resp.data["firstPKsName"]
                let secondPKsName = resp.data["secondPKsName"]
                let jsonData = resp.data["jsonData"]
                let attr = route.query['attributes'][0]
                let title = ""
                if (route.query['choice'] == "attributes") {
                    title = 'Chord Diagram -- ' + 'Entity ' + route.query['relation'] + ' for attributes (' + route.query['attributes'] + ')'
                } else {
                    if (route.query['type'] == 'one-many' || route.query['type'] == 'many-many') {
                        title = 'Sankey Diagram -- ' + 'Entity ' + route.query['relation'] + " \'s " + route.query['type'] + ' relationship: ' + route.query['anotherRelation'] + ' for attributes (' + route.query['attributes'] + ')'
                    } else {
                        title = 'Sankey Diagram -- ' + 'Entity ' + route.query['relation'] + " \'s " + route.query['type'] + ': ' + route.query['anotherRelation'] + ' for attributes (' + route.query['attributes'] + ')'
                    }
                }
                let dataSet = new Set();
                let links = [];
                for (const index in jsonData) {
                    const firstPK = jsonData[index][firstPKsName];
                    const secondPK = jsonData[index][secondPKsName];
                    if (dataSet.size >= 20) {
                        if (firstPK in dataSet && secondPK in dataSet) {
                            const link = {}
                            link['source'] = firstPK
                            link['target'] = secondPK
                            link['value'] = jsonData[index][attr]
                            links.push(link)
                        }
                    } else {
                        dataSet.add(firstPK)
                        dataSet.add(secondPK)
                        const link = {}
                        link['source'] = firstPK
                        link['target'] = secondPK
                        link['value'] = jsonData[index][attr]
                        links.push(link)
                    }
                }
                const dataset = []
                for (var key of Array.from(dataSet.values())) {
                    let data = {'name': key}
                    dataset.push(data)
                }
                var option = {
                    title: {
                        text: title,
                        left: 'center'
                    },
                    tooltip: {},
                    series: {
                        type: 'sankey',
                        data: dataset,
                        links: links,
                        nodeAlign: 'left'
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