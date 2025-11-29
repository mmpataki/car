<template>
  <div>
    <div :style="[showMore ? 'max-height: none' : 'max-height: 30px; overflow: hidden', textContainerStyle]" v-html="text">
    </div>
    <button v-if="!showMore && isOverflowed" @click="toggleShowMore" class="show-more-button">Show More</button>
    <button v-if="showMore" @click="toggleShowMore" class="show-less-button">Show Less</button>
  </div>
</template>

<script>
export default {
  props: {
    text: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      showMore: false,
      isOverflowed: false
    };
  },
  mounted() {
    this.checkOverflow();
  },
  computed: {
    textContainerStyle() {
      return {
        maxHeight: this.showMore ? 'none' : '30px',
        overflow: 'hidden',
        transition: 'box-shadow 0.3s ease-in-out'
      };
    }
  },
  methods: {
    toggleShowMore() {
      this.showMore = !this.showMore;
    },
    checkOverflow() {
      const el = this.$el.querySelector('div');
      this.isOverflowed = el.scrollHeight > el.clientHeight;
    }
  }
};
</script>

<style scoped>
.show-more-button,
.show-less-button {
  background: none;
  border: none;
  color: #007bff;
  cursor: pointer;
  font-size: 12px;
  padding: 0;
  margin: 0;
  text-decoration: underline;
}
.show-more-button:hover,
.show-less-button:hover {
  color: #0056b3;
}
</style>
