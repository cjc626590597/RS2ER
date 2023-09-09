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
                let pksName = resp.data["pksName"]
                let jsonData = resp.data["jsonData"].slice(0, 20)
                let attr1 = route.query['attributes'][0]
                let attr2 = route.query['attributes'][1]
                if (route.query['choice'] == "attributes") {
                    title.value = route.query['vsType'] + ' -- ' + 'Entity ' + route.query['relation'] + ' for attributes (' + route.query['attributes'] + ')'
                } else {
                    if (route.query['type'] == 'one-many' || route.query['type'] == 'many-many') {
                        title.value = route.query['vsType'] + ' -- ' + 'Entity ' + route.query['relation'] + " \'s " + route.query['type'] + ' relationship: ' + route.query['anotherRelation'] + ' for attributes (' + route.query['attributes'] + ')'
                    } else {
                        title.value = route.query['vsType'] + ' -- ' + 'Entity ' + route.query['relation'] + " \'s " + route.query['type'] + ': ' + route.query['anotherRelation'] + ' for attributes (' + route.query['attributes'] + ')'
                    }
                }
                let dataset1 = [];
                for (const index in jsonData) {
                    const data = {}
                    data['value'] = jsonData[index][attr1]
                    data['name'] = jsonData[index][pksName]
                    dataset1.push(data)
                }
                let dataset2 = [];
                for (const index in jsonData) {
                    const data = {}
                    data['value'] = jsonData[index][attr2]
                    data['name'] = jsonData[index][pksName]
                    dataset2.push(data)
                }
                var option = {
                    tooltip: {
                        trigger: 'item',
                        formatter: function (params) {
                            return (
                                pksName + ': ' + params.data['name'] + ' ' +
                                ' ( ' + params.data['value'] + ' ) '
                            );
                        }
                    },
                    legend: {
                        top: '5%',
                        left: 'center'
                    },
                    label: {
                        show: true,
                        formatter(param) {
                            // correct the percentage
                            return param.data['name'] + ' (' + param.data['value'] + ')';
                        }
                    },
                    series: [
                        {
                            name: 'Access From',
                            type: 'pie',
                            selectedMode: 'single',
                            radius: ['20%', '40%'],
                            label: {
                                position: 'inner',
                                fontSize: 14
                            },
                            labelLine: {
                                show: false
                            },
                            data: dataset1
                        },
                        {
                            name: 'Access From',
                            type: 'pie',
                            radius: ['45%', '65%'],
                            avoidLabelOverlap: false,
                            itemStyle: {
                                borderRadius: 10,
                                borderColor: '#fff',
                                borderWidth: 2
                            },
                            label: {
                                show: true,
                            },
                            emphasis: {
                                label: {
                                    show: true,
                                    fontSize: 40,
                                    fontWeight: 'bold'
                                }
                            },
                            labelLine: {
                                show: false
                            },
                            data: dataset2
                        }
                    ]
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