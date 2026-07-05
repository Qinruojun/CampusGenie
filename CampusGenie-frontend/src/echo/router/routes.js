const HomeView = () => import("../views/HomeView.vue");
const IslandExploreView = () => import("../views/IslandExploreView.vue");
const MyMapView = () => import("../views/MyMapView.vue");
const CreatePostView = () => import("../views/CreatePostView.vue");
const RoadmarksView = () => import("../views/RoadmarksView.vue");
const CreateNewIslandView = () => import("../views/CreateNewIslandView.vue");
const JazzView = () => import("../views/Islandview/jazz/JazzView.vue");
const JazzLibraryView = () => import("../views/Islandview/jazz/JazzLibrary.vue");
const CustomIslandView = () => import("../views/spaces/CustomIslandView.vue");
const DreamIslandView = () => import("../views/spaces/DreamIslandView.vue");
const FlowingCoastView = () => import("../views/spaces/FlowingCoastView.vue");
const HeardBayView = () => import("../views/spaces/HeardBayView.vue");
const HugStationView = () => import("../views/spaces/HugStationView.vue");
const LighthouseHillView = () => import("../views/spaces/LighthouseHillView.vue");
const QuietForestView = () => import("../views/spaces/QuietForestView.vue");
const WarmLampView = () => import("../views/spaces/WarmLampView.vue");

export const echoRouteNames = {
  home: "home",
  map: "map",
  myMap: "my-map",
  createPost: "create-post",
  roadmarks: "roadmarks",
  createIsland: "create-island",
  quietForest: "quiet-forest",
  lighthouseHill: "lighthouse-hill",
  heardBay: "heard-bay",
  hugStation: "hug-station",
  dreamIsland: "dream-island",
  jazz: "jazz",
  jazzLibrary: "jazz-library",
  flowingCoast: "flowing-coast",
  warmLamp: "warm-lamp",
  customIsland: "custom-island"
};

export const echoRoutes = [
  {
    path: "",
    name: echoRouteNames.home,
    component: HomeView,
    meta: { title: "精神空间" }
  },
  {
    path: "explore",
    name: echoRouteNames.map,
    component: IslandExploreView,
    meta: { title: "社区探索" }
  },
  {
    path: "my-map",
    name: echoRouteNames.myMap,
    component: MyMapView,
    meta: { title: "我的岛屿" }
  },
  {
    path: "spaces/quiet-forest",
    name: echoRouteNames.quietForest,
    component: QuietForestView,
    meta: { title: "安静森林", slug: "low-energy" }
  },
  {
    path: "spaces/lighthouse-hill",
    name: echoRouteNames.lighthouseHill,
    component: LighthouseHillView,
    meta: { title: "灯塔丘", slug: "graduate" }
  },
  {
    path: "spaces/heard-bay",
    name: echoRouteNames.heardBay,
    component: HeardBayView,
    meta: { title: "被听见湾", slug: "tree-hole" }
  },
  {
    path: "spaces/hug-station",
    name: echoRouteNames.hugStation,
    component: HugStationView,
    meta: { title: "拥抱站", slug: "tree-hole" }
  },
  {
    path: "spaces/dream-island",
    name: echoRouteNames.dreamIsland,
    component: DreamIslandView,
    meta: { title: "想象岛", slug: "music" }
  },
  {
    path: "spaces/jazz",
    name: echoRouteNames.jazz,
    component: JazzView,
    meta: { title: "爵士岛", slug: "music" }
  },
  {
    path: "spaces/jazz/library",
    name: echoRouteNames.jazzLibrary,
    component: JazzLibraryView,
    meta: { title: "爵士岛资源库", slug: "music" }
  },
  {
    path: "spaces/flowing-coast",
    name: echoRouteNames.flowingCoast,
    component: FlowingCoastView,
    meta: { title: "流动海岸", slug: "low-energy" }
  },
  {
    path: "spaces/warm-lamp",
    name: echoRouteNames.warmLamp,
    component: WarmLampView,
    meta: { title: "暖灯岛", slug: "tree-hole" }
  },
  {
    path: "spaces/custom/:id",
    name: echoRouteNames.customIsland,
    component: CustomIslandView,
    meta: { title: "自定义岛屿" }
  },
  {
    path: "create-post",
    name: echoRouteNames.createPost,
    component: CreatePostView,
    meta: { title: "发布情绪" }
  },
  {
    path: "roadmarks",
    name: echoRouteNames.roadmarks,
    component: RoadmarksView,
    meta: { title: "我的路标" }
  },
  {
    path: "create-island",
    name: echoRouteNames.createIsland,
    component: CreateNewIslandView,
    meta: { title: "创建岛屿" }
  },
  {
    path: ":pathMatch(.*)*",
    redirect: { name: echoRouteNames.home }
  }
];
