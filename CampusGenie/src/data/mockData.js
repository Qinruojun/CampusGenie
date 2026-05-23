export const quickActions = [
  { icon: '📅', label: '校历查询', question: '这学期的校历安排是什么？' },
  { icon: '🍚', label: '食堂菜单', question: '一食堂今天有什么菜？' },
  { icon: '📚', label: '图书馆', question: '图书馆周末开放吗？' },
  { icon: '🔧', label: '报修流程', question: '宿舍空调坏了怎么报修？' },
  { icon: '🚌', label: '校车时刻', question: '校车时刻表在哪里看？' },
  { icon: '💡', label: 'AI 问答', question: '新生入学需要准备什么？' }
]

export const categories = ['全部', '教务', '食堂', '图书馆', '宿舍', '交通', '社团', '就业']

export const hotQuestions = [
  { id: 1, title: '期末考试成绩什么时候出', count: 2300, category: '教务' },
  { id: 2, title: '暑假放假时间安排', count: 1800, category: '教务' },
  { id: 3, title: '图书馆借书流程', count: 1500, category: '图书馆' },
  { id: 4, title: '一食堂今日菜单', count: 1200, category: '食堂' },
  { id: 5, title: '校园卡挂失补办', count: 980, category: '生活' },
  { id: 6, title: '宿舍网络怎么报修', count: 860, category: '宿舍' },
  { id: 7, title: '校车时刻表在哪里看', count: 760, category: '交通' }
]

export const knowledgeItems = [
  { id: 101, question: '图书馆周末开放吗？', answer: '图书馆周六、周日开放时间为 8:30-21:30，法定节假日以学校通知为准。', category: '图书馆', source: '图书馆服务指南', status: '已发布', updatedAt: '2026-05-18' },
  { id: 102, question: '一食堂今天有什么菜？', answer: '一食堂菜单每日 9:00 前更新，可在校园生活百事通食堂栏目查看。', category: '食堂', source: '后勤公众号', status: '已发布', updatedAt: '2026-05-20' },
  { id: 103, question: '校园卡丢了怎么办？', answer: '可在校园卡服务中心或线上服务大厅挂失，补办需携带学生证或身份证。', category: '生活', source: '学生手册', status: '已发布', updatedAt: '2026-05-16' },
  { id: 104, question: '宿舍空调坏了怎么报修？', answer: '进入后勤报修系统，选择宿舍维修，填写楼栋、房间号和故障描述后提交。', category: '宿舍', source: '后勤服务平台', status: '待优化', updatedAt: '2026-05-14' },
  { id: 105, question: '在哪里打印成绩单？', answer: '学生可在教务处自助打印机打印成绩单，也可登录教务系统申请电子版。', category: '教务', source: '教务处网站', status: '已发布', updatedAt: '2026-05-12' }
]

export const auditItems = [
  { id: 201, contributor: '张同学', question: '社团招新一般什么时候开始？', answer: '社团集中招新通常在每学年开学后 2-3 周进行，具体以团委通知为准。', category: '社团', status: '待审核', createdAt: '2026-05-21' },
  { id: 202, contributor: '李同学', question: '晚上可以去操场跑步吗？', answer: '操场通常开放至 22:00，遇到大型活动或维修会临时调整。', category: '活动', status: '待审核', createdAt: '2026-05-21' },
  { id: 203, contributor: '王同学', question: '校医院周末上班吗？', answer: '校医院周末设有值班窗口，普通门诊时间以校医院公告为准。', category: '生活', status: '待审核', createdAt: '2026-05-20' }
]
