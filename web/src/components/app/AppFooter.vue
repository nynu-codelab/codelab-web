<template>
  <footer class="app-footer">
    <div class="app-footer__inner app-container">
      <div>
        <strong>NYNU Code Lab</strong>
        <p>南阳师范学院 Code Lab 实验室</p>
      </div>
      <p class="app-footer__notice">
        本网站为南阳师范学院 Code Lab 实验室自建展示站，非学校官方门户网站。
      </p>
    </div>
  </footer>
</template>

<style scoped>
.app-footer {
  position: relative;
  z-index: 2;
  padding: 46px 0;
  border-top: 1px solid rgba(153, 217, 255, 0.16);
  background: rgba(2, 6, 11, 0.74);
}

.app-footer__inner {
  display: flex;
  justify-content: space-between;
  gap: 30px;
}

.app-footer strong {
  color: var(--app-text-strong);
  font-size: 18px;
}

.app-footer p {
  color: var(--app-muted);
  font-size: 14px;
  line-height: 1.8;
}

.app-footer__notice {
  max-width: 520px;
  text-align: right;
}

@media (max-width: 720px) {
  .app-footer__inner {
    display: grid;
  }

  .app-footer__notice {
    text-align: left;
  }
}
</style>
