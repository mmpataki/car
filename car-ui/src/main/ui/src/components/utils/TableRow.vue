<template>
  <tr>
    <td v-for="(head, idx) in item.headers" :key="idx">
      <span v-if="!(item.item[head.value] instanceof Object)">
        {{ item.item[head.value] }}
      </span>
      <template v-else-if="item.item[head.value] instanceof Array">
        <div style="display: flex">
          <TableCell
            v-for="(item, idx) in item.item[head.value]"
            :key="idx"
            :item="item"
          ></TableCell>
        </div>
      </template>
      <TableCell v-else :item="item.item[head.value]"></TableCell>
    </td>
  </tr>
</template>

<script>
import TableCell from "./TableCell.vue";
export default {
  props: ["item"],
  components: { TableCell },
};
</script>

<style>
</style>