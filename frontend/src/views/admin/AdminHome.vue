<template>
  <div class="admin-home column">
    <nav>
      <div class="row">
        <button @click="handleLogout">退出</button>
      </div>
    </nav>

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

  nav {
    div.row {
      width: 100%;
      justify-content: flex-end;
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