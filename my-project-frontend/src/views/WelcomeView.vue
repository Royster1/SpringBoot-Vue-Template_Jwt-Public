<template>
  <div style="width: 100vw;height: 100vh;overflow: hidden;display: flex">
    <div style="flex: 1;background-color: black">
      <el-image style="width: 100%;height: 100%" fit="cover"
                src="https://ts1.cn.mm.bing.net/th/id/R-C.d75c22702f7283cd4847ce16d425e719?rik=h1RQVN7iVamDdw&riu=http%3a%2f%2fimg.pconline.com.cn%2fimages%2fupload%2fupc%2ftx%2fphotoblog%2f1107%2f12%2fc3%2f8303389_8303389_1310443757203.jpg&ehk=1zaNF1gkRprVQoFzq5j9lnE5%2fZuS9ecZelKci7baEXE%3d&risl=&pid=ImgRaw&r=0"/>
    </div>
    <!-- 绝对位置 -->
    <div class="welcome-title">
      <div style="font-size: 30px;font-weight: bold">欢迎来到我们的学习平台</div>
      <div style="margin-top: 10px">在这里你可以学习如何使用Java,如何搭建网站</div>
      <div style="margin-top: 5px">欢迎各位朋友</div>
    </div>
    <div class="right-card">
      <router-view v-slot="{Component}">
        <transition name="el-fade-in-linear" mode="out-in">
          <component :is="Component"/>
        </transition>
      </router-view>
    </div>
  </div>
</template>

<script>

</script>

<style scoped>
.right-card {
  width: 400px;
  z-index: 1;
  background-color: var(--el-bg-color);
}

.welcome-title {
  position: absolute;
  bottom: 30px;
  left: 30px;
  color: white;
  text-shadow: 0 0 10px black;
}
</style>