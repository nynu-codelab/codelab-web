<template>
  <footer id="footer" class="preview-footer">
    <div class="preview-footer__inner preview-container">
      <div>
        <strong>NYNU Code Lab</strong>
        <p>南阳师范学院 Code Lab 实验室</p>
      </div>
      <p class="preview-footer__notice">
        本网站为南阳师范学院 Code Lab 实验室自建展示站，非学校官方门户网站。
      </p>
    </div>
  </footer>
</template>

<style scoped>
.preview-footer {
  position: relative;
  z-index: 2;
  padding: 46px 0;
  border-top: 1px solid rgba(153, 217, 255, 0.16);
  background: rgba(2, 6, 11, 0.74);
}

.preview-footer__inner {
  display: flex;
  justify-content: space-between;
  gap: 30px;
}

.preview-footer strong {
  color: #f9fcff;
  font-size: 18px;
}

.preview-footer p {
  color: var(--preview-muted);
  font-size: 14px;
  line-height: 1.8;
}

.preview-footer__notice {
  max-width: 520px;
  text-align: right;
}

@media (max-width: 720px) {
  .preview-footer__inner {
    display: grid;
  }

  .preview-footer__notice {
    text-align: left;
  }
}
</style>
