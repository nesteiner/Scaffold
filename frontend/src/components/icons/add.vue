<template>
  <svg t="1687953669717" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="3104"><path d="M801.171 483.589H544V226.418c0-17.673-14.327-32-32-32s-32 14.327-32 32v257.171H222.83c-17.673 0-32 14.327-32 32s14.327 32 32 32H480v257.17c0 17.673 14.327 32 32 32s32-14.327 32-32v-257.17h257.171c17.673 0 32-14.327 32-32s-14.327-32-32-32z" fill="" p-id="3105"></path></svg>
</template>

<script lang="ts" setup>
import { computed } from 'vue';

interface PropsInterface {
  width: number
  height: number
  fill ?: string
}

const props = withDefaults(defineProps<PropsInterface>(), {
  fill: "white"
})

const width = computed(() => `${props.width}px`)
const height = computed(() => `${props.height}px`)
</script>

<style lang="scss" scoped>
svg {
  width: v-bind(width);
  height: v-bind(height);
  fill: v-bind(fill)
}
</style>