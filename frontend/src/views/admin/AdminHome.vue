<template>
  <div class="admin-home column">
    <div class="navbar">
      <a class="right" href="/" @click="handleLogout">退出</a>
    </div>

    <div class="row">
      <Sidebar>
        <SidebarItem @click="handleClickSidebar('student-manage')">学生管理</SidebarItem>
        <SidebarItem @click="handleClickSidebar('role-manage')">角色管理</SidebarItem>
      </Sidebar>

      <div class="content">
        <router-view/>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {Sidebar, SidebarItem} from "@/components/sidebar";
import {useRouter} from "vue-router";
import {logout} from "@/api";

const router = useRouter()

function handleClickSidebar(name: string) {
  router.push({name})
}

function handleLogout() {
  logout()
  router.replace({name: "login"})
}
</script>

<style lang="scss" scoped>
div.admin-home.column {
  display: flex;
  flex-direction: column;

  .navbar {
    background: rgba(0, 0, 0, 0.1);

    a {
      float: left;
      display: block;
      text-align: center;
      padding: 14px 20px;
      text-decoration: none;

      &.right {
        float: right;
      }
    }
  }

  div.row {
    display: flex;

    div.content {
      flex-grow: 1;
      flex-shrink: 1;
      flex-basis: auto;
    }
  }

}
</style>