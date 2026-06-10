import { defineStore } from 'pinia'
const STORAGE_KEY = 'campusgenie:knowledge-edit-cache'

function readStorage(){
    const raw = sessionStorage.getItem(STORAGE_KEY)

    if(!raw){
        return {}
    }
    try {JSON.parse(raw)}
    catch(error){
        console.error(error)
        return {}
    }
}
function writeStorage(data){
    sessionStorage.setItem(STORAGE_KEY,JSON.stringify(data))
}
//创建一个名字叫knowledgeEdit的Pinia仓库
export const useKnowledgeEditStore = defineStore('knowledgeEdit',{
    state:()=>({//仓库数据存到state里面,只会在第一次创建时执行一次，确保只有一个实例
      knowledgeMap:readStorage()//这是响应式的
    }),
    actions: {
        setKnowledge(item) {
            if (!item || item.id === undefined || item.id === null) {
                return
            }

            const id = String(item.id)

            this.knowledgeMap = {
                ...this.knowledgeMap,//扩展运算符，将原来的数据全都粘进来，就是{ ...old, [id]: item }
            }

            writeStorage(this.knowledgeMap)
        },

        getKnowledge(id) {
            if (id === undefined || id === null) {
                return null
            }

            return this.knowledgeMap[String(id)] || null//JSON的key必须是字符串
        },//需要用vue 的computed 来监听这个数据，才能变成响应式数据

        removeKnowledge(id) {
            if (id === undefined || id === null) {
                return
            }

            const newMap = { ...this.knowledgeMap }
            delete newMap[String(id)]

            this.knowledgeMap = newMap
            writeStorage(this.knowledgeMap)
        },

        clearKnowledgeCache() {
            this.knowledgeMap = {}
            sessionStorage.removeItem(STORAGE_KEY)
        }
    }

})
