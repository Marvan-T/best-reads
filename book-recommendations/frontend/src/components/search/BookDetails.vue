<template>
  <v-hover v-slot="{ hover }">
    <v-col
      class="mt-12"
      cols="12"
    >
      <v-row>
        <v-card
          class="full-width-card"
          :color="getHoverEffect(hover)"
          outlined
          flat
          @click="emitViewBook"
        >
          <v-col cols="2">
            <p>Some text</p>
          </v-col>
          <v-col
            cols="6"
          >
            <p>Text 2 </p>
          </v-col>
        </v-card>
      </v-row>
    </v-col>
  </v-hover>
</template>

<script>
import {EventBus} from "@/event-bus";

export default {
  name: 'BookDetails',
  props: {
    title: {
      type: String,
      default: 'Book title'
    },
    authors: {
      type: String,
      default: 'Author'
    },
    publishedDate: {
      type: String,
      default: 'Published date'
    },
    thumbnail: {
      type: String,
      default: 'https://storage.googleapis.com/du-prd/books/images/9781538719824.jpg'
    },
    isbn: {
      type: String,
      required: true
    },
    origin: {
      type: String, // from which page the book is being viewed (effect how the book data is displayed on view book)
      required: true // either 'search' or 'other'
    },
    bookData: {
      type: Object,
      required: true
    },
    selectable: {
      type: Boolean,
      required: false,
      default: false
    }
  },
  data() {
    return {
      selected: false,
    };
  },
  computed: {
    getTruncatedAuthor() {
      return this.authors === ""
        ? this.formatDate()
        : this.authorsWithFormattedDate();
    },
    getTruncatedDescription() {
      const maxCharacterCount = 100;
      if (this.bookData.description.length > maxCharacterCount) {
        return this.bookData.description.substring(0, maxCharacterCount) + '...'
      }
      return this.bookData.description;
    }
  },
  deactivated() {
    //clear the hover effect when navigating away from the page
    this.$refs.hoverEffectRef._data.isActive = false;
  },
  methods: {
    formatDate() {
      // If there's no published date, return an empty string
      if (this.publishedDate === null || this.publishedDate === "") {
        return "";
      }

      // Return the formatted year of the published date
      const dateToFormat = new Date(this.publishedDate);
      return dateToFormat.getFullYear();
    },
    authorsWithFormattedDate() {
      // Return the truncated authors with the formatted date
      const truncatedAuthors = this.truncateText(this.authors, 30);
      const datePart = this.publishedDate === null ? "" : `- ${this.formatDate()}`;

      return `${truncatedAuthors} ${datePart}`;
    },
    truncateText(text, maxCharacterCount) {
      if (text.length > maxCharacterCount) {
        return text.substring(0, maxCharacterCount) + '...'
      }
      return text
    },
    async emitViewBook() {
      await this.$router.push({name: 'book'});
      switch (this.origin) {
        case 'search':
          EventBus.$emit('view-book', this.bookData);
          break;
        case 'other':
          EventBus.$emit('view-book-other', this.bookData);
          break;
      }
    },
    changeSelected() {
      if (this.selectable) {
        this.selected = !this.selected;
        this.$emit(this.selected ? "selected" : "unselected", this.isbn, this.title);
      }
    },
    getHoverEffect(hover) {
      return hover ? "blue-grey lighten-4" : "#FFFFFF";
    }
  }
}
</script>

<style scoped>

.full-width-card {
  width: 100%;
}


</style>
