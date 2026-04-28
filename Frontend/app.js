// ═══════════════════════════════════════════════════
// CONFIG & STATE
// ═══════════════════════════════════════════════════
const API = 'http://localhost:8080/api';
let state = {
  token: localStorage.getItem('sa_token') || null,
  user: JSON.parse(localStorage.getItem('sa_user') || 'null'),
  allAlumni: [],
  regRole: 'STUDENT',
  editingEventId: null,
  currentMentorTab: 'student',
  currentAdminTab: 'users'
};

// ═══════════════════════════════════════════════════
// UTILS
// ═══════════════════════════════════════════════════
function api(path, opts = {}) {
  const headers = { 'Content-Type': 'application/json' };
  if (state.token) headers['Authorization'] = 'Bearer ' + state.token;
  return fetch(API + path, { ...opts, headers: { ...headers, ...opts.headers } })
    .then(async r => {
      if (!r.ok) {
        const err = await r.json().catch(() => ({ message: 'Server error' }));
        throw new Error(err.message || err.error || 'Request failed');
      }
      return r.json().catch(() => ({}));
    });
}

function toast(msg, type = 'info') {
  const el = document.createElement('div');
  el.className = 'toast ' + type;
  el.textContent = msg;
  document.getElementById('toast-container').appendChild(el);
  setTimeout(() => el.remove(), 3500);
}

function openModal(id) { document.getElementById(id).classList.add('open'); }
function closeModal(id) { document.getElementById(id).classList.remove('open'); }
document.querySelectorAll('.modal-overlay').forEach(m => {
  m.addEventListener('click', e => { if (e.target === m) m.classList.remove('open'); });
});

function avatarLetter(name) { return (name || 'U')[0].toUpperCase(); }

function formatDate(d) {
  if (!d) return '—';
  try { return new Date(d).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' }); } catch { return d; }
}

function statusBadge(s) {
  const map = { PENDING: 'badge-yellow', APPROVED: 'badge-green', REJECTED: 'badge-red' };
  return `<span class="badge ${map[s] || 'badge-blue'}">${s || '—'}</span>`;
}

// ═══════════════════════════════════════════════════
// NAV & PAGES
// ═══════════════════════════════════════════════════
function showPage(name) {
  document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
  document.getElementById('page-' + name).classList.add('active');
  document.querySelectorAll('.nav-link').forEach(l => l.classList.remove('active'));
  // Load data on navigate
  if (name === 'alumni') loadAlumni();
  if (name === 'events') loadEvents();
  if (name === 'jobs') loadJobs();
  if (name === 'mentorship') loadMentorship();
  if (name === 'profile') loadProfile();
  if (name === 'admin') loadAdminUsers();
  window.scrollTo(0, 0);
}

function toggleMobile() { document.getElementById('mobile-menu').classList.toggle('open'); }
function closeMobile() { document.getElementById('mobile-menu').classList.remove('open'); }

function updateNav() {
  const u = state.user;
  const loggedIn = !!u;
  document.getElementById('nav-auth').style.display = loggedIn ? 'none' : 'flex';
  document.getElementById('nav-user').style.display = loggedIn ? 'flex' : 'none';
  document.getElementById('mob-login').style.display = loggedIn ? 'none' : 'block';
  document.getElementById('mob-logout').style.display = loggedIn ? 'block' : 'none';
  if (loggedIn) {
    document.getElementById('nav-avatar').textContent = avatarLetter(u.fullName);
    document.getElementById('nav-user-name').textContent = u.fullName;
    const isAdmin = u.role === 'ADMIN';
    const isAlumni = u.role === 'ALUMNI';
    const isStudent = u.role === 'STUDENT';


    // Show nav links
    const show = (id, cond) => {
      document.getElementById(id).style.display = cond ? '' : 'none';
      const mob = document.getElementById('mob-' + id.replace('nav-', ''));
      if (mob) mob.style.display = cond ? '' : 'none';
    };
    show('nav-alumni', !isAdmin);
    show('nav-events', true);
    show('nav-jobs', true);
    show('nav-mentorship', !isAdmin);
    show('nav-profile', !isAdmin);
    show('nav-admin', isAdmin);


    // Page-specific buttons
    if (document.getElementById('events-admin-btn'))
      document.getElementById('events-admin-btn').style.display = isAdmin ? 'block' : 'none';
    if (document.getElementById('post-job-btn'))
      document.getElementById('post-job-btn').style.display = (isAlumni || isAdmin) ? '' : 'none';
    if (document.getElementById('req-mentorship-btn'))
      document.getElementById('req-mentorship-btn').style.display = isStudent ? '' : 'none';
  } else {
    ['nav-alumni', 'nav-events', 'nav-jobs', 'nav-mentorship', 'nav-profile', 'nav-admin'].forEach(id => {
      document.getElementById(id).style.display = 'none';
    });
  }
}

// ═══════════════════════════════════════════════════
// AUTH
// ═══════════════════════════════════════════════════
function setRegRole(role, el) {
  state.regRole = role;
  document.querySelectorAll('.role-btn').forEach(b => b.classList.remove('active'));
  el.classList.add('active');
  document.getElementById('reg-extra-student').style.display = role === 'STUDENT' ? '' : 'none';
  document.getElementById('reg-extra-alumni').style.display = role === 'ALUMNI' ? '' : 'none';
}

function doLogin() {
  const email = document.getElementById('login-email').value.trim();
  const password = document.getElementById('login-password').value;
  if (!email || !password) return toast('Fill in all fields', 'error');
  const btn = document.getElementById('login-btn');
  btn.disabled = true; btn.textContent = 'Signing in...';
  api('/auth/login', { method: 'POST', body: JSON.stringify({ email, password }) })
    .then(res => {
      const data = res.data || res;
      state.token = data.token;
      state.user = { userId: data.userId, fullName: data.fullName, email: data.email, role: data.role };
      localStorage.setItem('sa_token', state.token);
      localStorage.setItem('sa_user', JSON.stringify(state.user));
      closeModal('modal-login');
      toast('Welcome back, ' + data.fullName + '!', 'success');
      updateNav();
      if (data.role === 'ADMIN') showPage('admin');
      else showPage('alumni');
    })
    .catch(e => toast(e.message, 'error'))
    .finally(() => { btn.disabled = false; btn.textContent = 'Sign In'; });
}

function doRegister() {
  const body = {
    fullName: document.getElementById('reg-name').value.trim(),
    email: document.getElementById('reg-email').value.trim(),
    password: document.getElementById('reg-password').value,
    role: state.regRole
  };
  if (state.regRole === 'STUDENT') {
    body.department = document.getElementById('reg-dept').value;
    body.graduationYear = document.getElementById('reg-grad').value;
  } else if (state.regRole === 'ALUMNI') {
    body.company = document.getElementById('reg-company').value;
    body.jobTitle = document.getElementById('reg-job').value;
    body.department = document.getElementById('reg-dept2').value;
    body.graduationYear = document.getElementById('reg-grad2').value;
  }
  if (!body.fullName || !body.email || !body.password) return toast('Fill required fields', 'error');
  api('/auth/register', { method: 'POST', body: JSON.stringify(body) })
    .then(() => {
      toast('Registration successful! Please sign in.', 'success');
      closeModal('modal-register');
      openModal('modal-login');
    })
    .catch(e => toast(e.message, 'error'));
}

function doForgotPassword() {
  const email = document.getElementById('forgot-email').value.trim();
  if (!email) return toast('Enter your email', 'error');
  api('/auth/forgot-password?email=' + encodeURIComponent(email), { method: 'POST' })
    .then(() => { toast('Reset link sent to your email!', 'success'); closeModal('modal-forgot'); })
    .catch(e => toast(e.message, 'error'));
}

function doLogout() {
  state.token = null; state.user = null;
  localStorage.removeItem('sa_token'); localStorage.removeItem('sa_user');
  updateNav();
  showPage('home');
  toast('Logged out successfully');
}

// ═══════════════════════════════════════════════════
// ALUMNI
// ═══════════════════════════════════════════════════
function loadAlumni() {
  document.getElementById('alumni-grid').innerHTML = '<div class="loading-overlay"><span class="spinner"></span> Loading alumni...</div>';
  api('/alumni/all')
    .then(res => {
      state.allAlumni = res.data || [];
      renderAlumni(state.allAlumni);
    })
    .catch(e => { toast(e.message, 'error'); document.getElementById('alumni-grid').innerHTML = '<div class="empty"><div class="empty-icon">😕</div><p>Could not load alumni. Make sure backend is running.</p></div>'; });
}

function renderAlumni(list) {
  const g = document.getElementById('alumni-grid');
  if (!list.length) { g.innerHTML = '<div class="empty"><div class="empty-icon">👥</div><p>No alumni found</p></div>'; return; }
  g.innerHTML = list.map(a => `
    <div class="card alumni-card">
      <div class="alumni-head">
        <div class="alumni-avatar">${avatarLetter(a.fullName)}</div>
        <div>
          <div class="alumni-name">${a.fullName || '—'}</div>
          <div class="alumni-role">${a.jobTitle || ''} ${a.company ? '@ ' + a.company : ''}</div>
        </div>
        <div style="margin-left:auto;">${a.role === 'ALUMNI' ? '<span class="badge badge-blue">Alumni</span>' : '<span class="badge badge-purple">Student</span>'}</div>
      </div>
      <div class="alumni-meta">
        ${a.department ? `<span class="badge badge-purple">📚 ${a.department}</span>` : ''}
        ${a.graduationYear ? `<span class="badge badge-yellow">🎓 ${a.graduationYear}</span>` : ''}
        ${a.location ? `<span class="badge badge-green">📍 ${a.location}</span>` : ''}
      </div>
      ${a.skills ? `<div class="alumni-skills">${a.skills.split(',').map(s => `<span class="skill-tag">${s.trim()}</span>`).join('')}</div>` : ''}
      ${a.role === 'ALUMNI' ? `<button class="btn btn-ghost btn-sm" onclick="openMentorshipWith(${a.id})">🤝 Request Mentorship</button>` : ''}
    </div>
  `).join('');
}

function filterAlumni() {
  const q = document.getElementById('alumni-search').value.toLowerCase();
  const filtered = state.allAlumni.filter(a =>
    (a.fullName || '').toLowerCase().includes(q) ||
    (a.company || '').toLowerCase().includes(q) ||
    (a.skills || '').toLowerCase().includes(q) ||
    (a.department || '').toLowerCase().includes(q)
  );
  renderAlumni(filtered);
}

function setAlumniFilter(type, el) {
  document.querySelectorAll('.chip').forEach(c => c.classList.remove('active'));
  el.classList.add('active');
  if (type === 'all') renderAlumni(state.allAlumni);
}

function promptFilter(type) {
  const val = prompt('Filter by ' + type + ':');
  if (!val) return;
  api('/alumni/' + type + '?value=' + encodeURIComponent(val))
    .then(r => renderAlumni(r.data || []))
    .catch(e => toast(e.message, 'error'));
}

function openMentorshipWith(mentorId) {
  if (!state.user) { toast('Please sign in first', 'error'); openModal('modal-login'); return; }
  document.getElementById('mentor-id').value = mentorId;
  openModal('modal-mentorship');
}

// ═══════════════════════════════════════════════════
// EVENTS
// ═══════════════════════════════════════════════════
function loadEvents() {
  document.getElementById('events-grid').innerHTML = '<div class="loading-overlay"><span class="spinner"></span> Loading events...</div>';
  api('/events')
    .then(res => {
      const list = Array.isArray(res) ? res : (res.data || []);
      renderEvents(list);
    })
    .catch(e => { toast(e.message, 'error'); document.getElementById('events-grid').innerHTML = '<div class="empty"><div class="empty-icon">📅</div><p>Could not load events</p></div>'; });
  // Show/hide admin button after nav update
  if (state.user?.role === 'ADMIN')
    document.getElementById('events-admin-btn').style.display = 'block';
}

function renderEvents(list) {
  const g = document.getElementById('events-grid');
  if (!list.length) { g.innerHTML = '<div class="empty"><div class="empty-icon">📅</div><p>No events scheduled</p></div>'; return; }
  const isAdmin = state.user?.role === 'ADMIN';
  g.innerHTML = list.map(e => `
    <div class="card">
      <div class="event-date">📅 ${formatDate(e.eventDate)}</div>
      <div class="event-title">${e.title}</div>
      <div class="event-loc">📍 ${e.location || 'TBD'}</div>
      <p style="font-size:13px;color:var(--muted2);line-height:1.5;margin-bottom:14px;">${e.description || ''}</p>
      <div style="display:flex;gap:8px;flex-wrap:wrap;">
        ${state.user ? `<button class="btn btn-success btn-sm" onclick="registerEvent(${e.id})">✓ Register</button>` : ''}
        ${isAdmin ? `<button class="btn btn-ghost btn-sm" onclick="viewParticipants(${e.id})">👥 Participants</button>` : ''}
        ${isAdmin ? `<button class="btn btn-ghost btn-sm" onclick="editEvent(${e.id},'${encodeURIComponent(JSON.stringify(e))}')">✏️ Edit</button>` : ''}
        ${isAdmin ? `<button class="btn btn-danger btn-sm" onclick="deleteEvent(${e.id})">🗑 Delete</button>` : ''}
      </div>
    </div>
  `).join('');
}

function openEventModal(id, data) {
  state.editingEventId = id || null;
  document.getElementById('event-modal-title').textContent = id ? 'Edit Event' : 'Create Event';
  document.getElementById('event-save-btn').textContent = id ? 'Update Event' : 'Create Event';
  if (data) {
    document.getElementById('event-title').value = data.title || '';
    document.getElementById('event-desc').value = data.description || '';
    document.getElementById('event-loc').value = data.location || '';
    document.getElementById('event-date').value = data.eventDate || '';
  } else {
    document.getElementById('event-title').value = '';
    document.getElementById('event-desc').value = '';
    document.getElementById('event-loc').value = '';
    document.getElementById('event-date').value = '';
  }
  openModal('modal-event');
}

function saveEvent() {
  const body = {
    title: document.getElementById('event-title').value,
    description: document.getElementById('event-desc').value,
    location: document.getElementById('event-loc').value,
    eventDate: document.getElementById('event-date').value
  };
  if (!body.title) return toast('Title is required', 'error');
  const isEdit = !!state.editingEventId;
  const req = isEdit
    ? api('/events/' + state.editingEventId, { method: 'PUT', body: JSON.stringify(body) })
    : api('/events', { method: 'POST', body: JSON.stringify(body) });
  req.then(() => {
    toast(isEdit ? 'Event updated!' : 'Event created!', 'success');
    closeModal('modal-event');
    loadEvents();
  }).catch(e => toast(e.message, 'error'));
}

function editEvent(id, encoded) {
  const data = JSON.parse(decodeURIComponent(encoded));
  openEventModal(id, data);
}

function deleteEvent(id) {
  if (!confirm('Delete this event?')) return;
  api('/events/' + id, { method: 'DELETE' })
    .then(() => { toast('Event deleted', 'success'); loadEvents(); })
    .catch(e => toast(e.message, 'error'));
}

function registerEvent(id) {
  if (!state.user) { toast('Sign in first', 'error'); return; }
  api('/events/register', { method: 'POST', body: JSON.stringify({ userId: state.user.userId, eventId: id }) })
    .then(r => { const msg = typeof r === 'string' ? r : (r.message || 'Registered!'); toast(msg, 'success'); })
    .catch(e => toast(e.message, 'error'));
}

function viewParticipants(eventId) {
  api('/events/' + eventId + '/participants')
    .then(res => {
      const list = Array.isArray(res) ? res : (res.data || []);
      alert('Participants (' + list.length + '):\n' + list.map(p => p.user?.fullName || p.userId).join('\n'));
    })
    .catch(e => toast(e.message, 'error'));
}

// ═══════════════════════════════════════════════════
// JOBS
// ═══════════════════════════════════════════════════
function loadJobs() {
  document.getElementById('jobs-grid').innerHTML = '<div class="loading-overlay"><span class="spinner"></span> Loading jobs...</div>';
  api('/jobs')
    .then(res => {
      const list = Array.isArray(res) ? res : (res.data || []);
      renderJobs(list);
    })
    .catch(e => { toast(e.message, 'error'); document.getElementById('jobs-grid').innerHTML = '<div class="empty"><div class="empty-icon">💼</div><p>Could not load jobs</p></div>'; });
  if (state.user && (state.user.role === 'ALUMNI' || state.user.role === 'ADMIN'))
    document.getElementById('post-job-btn').style.display = '';
}

function renderJobs(list) {
  const g = document.getElementById('jobs-grid');
  if (!list.length) { g.innerHTML = '<div class="empty"><div class="empty-icon">💼</div><p>No job postings yet</p></div>'; return; }
  g.innerHTML = list.map(j => `
    <div class="card">
      <div class="job-company">${j.company || '—'}</div>
      <div class="job-title">${j.title || '—'}</div>
      <div class="job-desc">${j.description || ''}</div>
      ${state.user ? `<button class="btn btn-primary btn-sm" onclick="applyJob(${j.id})">Apply Now →</button>` : '<span class="badge badge-blue">Sign in to apply</span>'}
    </div>
  `).join('');
}

function postJob() {
  const body = {
    title: document.getElementById('job-title').value,
    company: document.getElementById('job-company').value,
    description: document.getElementById('job-desc').value
  };
  if (!body.title) return toast('Job title required', 'error');
  api('/jobs', { method: 'POST', body: JSON.stringify(body) })
    .then(() => { toast('Job posted!', 'success'); closeModal('modal-job'); loadJobs(); })
    .catch(e => toast(e.message, 'error'));
}

function applyJob(jobId) {
  if (!state.user) { toast('Sign in first', 'error'); return; }
  api('/jobs/apply', { method: 'POST', body: JSON.stringify({ jobId, userId: state.user.userId }) })
    .then(r => { const msg = typeof r === 'string' ? r : 'Applied successfully!'; toast(msg, 'success'); })
    .catch(e => toast(e.message, 'error'));
}

// ═══════════════════════════════════════════════════
// MENTORSHIP
// ═══════════════════════════════════════════════════
function switchMentorTab(tab, el) {
  state.currentMentorTab = tab;
  document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
  el.classList.add('active');
  loadMentorship();
}

function loadMentorship() {
  const list = document.getElementById('mentorship-list');
  list.innerHTML = '<div class="loading-overlay"><span class="spinner"></span> Loading...</div>';
  const path = state.currentMentorTab === 'student' ? '/mentorship/student/my-requests' : '/mentorship/mentor/my-requests';
  api(path)
    .then(res => renderMentorship(res.data || []))
    .catch(e => { toast(e.message, 'error'); list.innerHTML = '<div class="empty"><div class="empty-icon">🤝</div><p>No mentorship requests found</p></div>'; });
  if (state.user?.role === 'STUDENT')
    document.getElementById('req-mentorship-btn').style.display = '';
}

function renderMentorship(list) {
  const g = document.getElementById('mentorship-list');
  if (!list.length) { g.innerHTML = '<div class="empty"><div class="empty-icon">🤝</div><p>No mentorship requests yet</p></div>'; return; }
  const isMentor = state.currentMentorTab === 'mentor';
  g.innerHTML = list.map(m => `
    <div class="card mentorship-card" style="margin-bottom:12px;">
      <div class="mentorship-status">
        <div class="mentorship-topic">${m.topic || 'General Mentorship'}</div>
        ${statusBadge(m.status)}
      </div>
      <div class="mentorship-msg">${m.message || ''}</div>
      <div style="font-size:12px;color:var(--muted);">
        ${m.scheduledAt ? `📅 Scheduled: ${formatDate(m.scheduledAt)}` : ''}
        ${m.meetingType ? ` · ${m.meetingType}` : ''}
        ${m.meetingLink ? ` · <a href="${m.meetingLink}" target="_blank" style="color:var(--accent);">Join</a>` : ''}
      </div>
      ${m.feedback ? `<div style="font-size:13px;color:var(--muted2);">⭐ ${m.rating}/5 — ${m.feedback}</div>` : ''}
      <div class="mentorship-actions">
        ${isMentor && m.status === 'PENDING' ? `
          <button class="btn btn-success btn-sm" onclick="openScheduleModal(${m.id})">✓ Accept & Schedule</button>
          <button class="btn btn-danger btn-sm" onclick="rejectMentorship(${m.id})">✕ Reject</button>
        ` : ''}
        ${!isMentor && m.status === 'APPROVED' && !m.feedback ? `
          <button class="btn btn-ghost btn-sm" onclick="openFeedback(${m.id})">⭐ Leave Feedback</button>
        ` : ''}
      </div>
    </div>
  `).join('');
}

function sendMentorshipRequest() {
  if (!state.user) return toast('Sign in first', 'error');
  const body = {
    studentId: state.user.userId,
    mentorId: parseInt(document.getElementById('mentor-id').value),
    topic: document.getElementById('mentor-topic').value,
    message: document.getElementById('mentor-msg').value
  };
  if (!body.mentorId || !body.topic) return toast('Fill all fields', 'error');
  api('/mentorship/request', { method: 'POST', body: JSON.stringify(body) })
    .then(() => { toast('Mentorship request sent!', 'success'); closeModal('modal-mentorship'); loadMentorship(); })
    .catch(e => toast(e.message, 'error'));
}

function openScheduleModal(id) {
  document.getElementById('schedule-mentorship-id').value = id;
  openModal('modal-schedule');
}

function acceptAndSchedule() {
  const id = document.getElementById('schedule-mentorship-id').value;
  const body = {
    meetingType: document.getElementById('schedule-type').value,
    meetingLink: document.getElementById('schedule-link').value,
    scheduledAt: document.getElementById('schedule-at').value
  };
  api('/mentorship/accept-and-schedule/' + id, { method: 'PUT', body: JSON.stringify(body) })
    .then(() => { toast('Session scheduled!', 'success'); closeModal('modal-schedule'); loadMentorship(); })
    .catch(e => toast(e.message, 'error'));
}

function rejectMentorship(id) {
  if (!confirm('Reject this request?')) return;
  api('/mentorship/reject/' + id, { method: 'PUT' })
    .then(() => { toast('Request rejected'); loadMentorship(); })
    .catch(e => toast(e.message, 'error'));
}

function openFeedback(id) {
  document.getElementById('feedback-id').value = id;
  openModal('modal-feedback');
}

function submitFeedback() {
  const id = document.getElementById('feedback-id').value;
  const body = { feedback: document.getElementById('feedback-msg').value, rating: parseInt(document.getElementById('feedback-rating').value) };
  api('/mentorship/feedback/' + id, { method: 'POST', body: JSON.stringify(body) })
    .then(() => { toast('Feedback submitted!', 'success'); closeModal('modal-feedback'); loadMentorship(); })
    .catch(e => toast(e.message, 'error'));
}

// ═══════════════════════════════════════════════════
// PROFILE
// ═══════════════════════════════════════════════════
function loadProfile() {
  if (!state.user) return;

  const id = state.user.userId;

  document.getElementById('profile-fullname').textContent = state.user.fullName;
  document.getElementById('profile-email-disp').textContent = state.user.email;
  document.getElementById('profile-role-disp').innerHTML =
    `<span class="badge badge-blue">${state.user.role}</span>`;
  document.getElementById('profile-avatar-big').textContent =
    avatarLetter(state.user.fullName);

  api('/profile/' + id)
    .then(res => {
      currentProfile = res.data || res;
      renderProfileDetails(currentProfile);
    })
    .catch(() => {
      // IMPORTANT FIX
      currentProfile = null;

      document.getElementById('profile-details').innerHTML =
        '<p style="color:var(--muted);font-size:14px;">No profile created yet. Click Edit Profile to create one.</p>';

      document.getElementById('profile-skills-display').innerHTML = '';
    });
}

function renderProfileDetails(p) {
  if (!p) return;
  document.getElementById('profile-details').innerHTML = `
    <div style="display:flex;flex-direction:column;gap:10px;font-size:14px;">
      ${p.bio ? `<div><span style="color:var(--muted2);">Bio</span><p style="margin-top:4px;line-height:1.6;">${p.bio}</p></div>` : ''}
      ${p.currentRole ? `<div><span style="color:var(--muted2);">Role</span><p style="margin-top:2px;font-weight:500;">${p.currentRole}</p></div>` : ''}
      ${p.currentCompany ? `<div><span style="color:var(--muted2);">Company</span><p style="margin-top:2px;">${p.currentCompany}</p></div>` : ''}
      ${p.experience ? `<div><span style="color:var(--muted2);">Experience</span><p style="margin-top:2px;">${p.experience}</p></div>` : ''}
      ${p.resumeUrl ? `<div><span style="color:var(--muted2);">Resume</span><p style="margin-top:2px;"><a href="${p.resumeUrl}" target="_blank" style="color:var(--accent);">View Resume ↗</a></p></div>` : ''}
    </div>
  `;
  if (p.skills) {
    document.getElementById('profile-skills-display').innerHTML = p.skills.split(',').map(s => `<span class="skill-tag">${s.trim()}</span>`).join('');
  }
}

function openProfileModal() {
  const p = currentProfile || {};
  document.getElementById('p-bio').value = p.bio || '';
  document.getElementById('p-skills').value = p.skills || '';
  document.getElementById('p-company').value = p.currentCompany || '';
  document.getElementById('p-role').value = p.currentRole || '';
  document.getElementById('p-exp').value = p.experience || '';
  document.getElementById('p-resume').value = p.resumeUrl || '';
  openModal('modal-profile');
}

function saveProfile() {
  if (!state.user) {
    toast('Please login first', 'error');
    return;
  }

  const body = {
    bio: document.getElementById('p-bio').value,
    skills: document.getElementById('p-skills').value,
    currentCompany: document.getElementById('p-company').value,
    currentRole: document.getElementById('p-role').value,
    experience: document.getElementById('p-exp').value,
    resumeUrl: document.getElementById('p-resume').value
  };

  const id = state.user.userId;

  const method = currentProfile ? 'PUT' : 'POST';

  api('/profile/' + id, {
    method: method,
    body: JSON.stringify(body)
  })
    .then(() => {
      toast(currentProfile ? 'Profile updated successfully ✅' : 'Profile created successfully ✅', 'success');
      closeModal('modal-profile');
      loadProfile();
    })
    .catch(e => {
      console.error(e);
      toast(e.message || 'Failed to save profile ❌', 'error');
    });
}

// ═══════════════════════════════════════════════════
// ADMIN
// ═══════════════════════════════════════════════════
function switchAdminTab(tab, el) {
  state.currentAdminTab = tab;
  document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
  el.classList.add('active');
  if (tab === 'users') loadAdminUsers();
  else if (tab === 'events') { loadEvents(); renderAdminEvents(); }
  else if (tab === 'jobs') renderAdminJobs();
}

function loadAdminUsers() {
  document.getElementById('admin-content').innerHTML = '<div class="loading-overlay"><span class="spinner"></span> Loading users...</div>';
  api('/admin/users')
    .then(res => {
      const list = Array.isArray(res) ? res : (res.data || []);
      if (!list.length) { document.getElementById('admin-content').innerHTML = '<div class="empty"><div class="empty-icon">👤</div><p>No users found</p></div>'; return; }
      document.getElementById('admin-content').innerHTML = `
        <div class="table-wrap">
          <table>
            <thead><tr><th>ID</th><th>Name</th><th>Email</th><th>Role</th><th>Actions</th></tr></thead>
            <tbody>${list.map(u => `
              <tr>
                <td style="color:var(--muted)">${u.id}</td>
                <td><div style="font-weight:500;">${u.fullName || '—'}</div></td>
                <td style="color:var(--muted2)">${u.email}</td>
                <td>${statusBadge2(u.role)}</td>
                <td><button class="btn btn-danger btn-sm" onclick="deleteUser(${u.id})">Delete</button></td>
              </tr>
            `).join('')}</tbody>
          </table>
        </div>
      `;
    })
    .catch(e => { toast(e.message, 'error'); document.getElementById('admin-content').innerHTML = '<div class="empty"><div class="empty-icon">😕</div><p>Could not load users</p></div>'; });
}

function statusBadge2(r) {
  const map = { ADMIN: 'badge-red', ALUMNI: 'badge-blue', STUDENT: 'badge-purple' };
  return `<span class="badge ${map[r] || 'badge-blue'}">${r || '—'}</span>`;
}

function deleteUser(id) {
  if (!confirm('Delete this user? This cannot be undone.')) return;
  api('/admin/delete/' + id, { method: 'DELETE' })
    .then(() => { toast('User deleted', 'success'); loadAdminUsers(); })
    .catch(e => toast(e.message, 'error'));
}

function renderAdminEvents() {
  api('/events').then(res => {
    const list = Array.isArray(res) ? res : (res.data || []);
    document.getElementById('admin-content').innerHTML = `
      <div style="margin-bottom:12px;"><button class="btn btn-primary btn-sm" onclick="openEventModal()">+ Create Event</button></div>
      <div class="table-wrap">
        <table>
          <thead><tr><th>ID</th><th>Title</th><th>Date</th><th>Location</th><th>Actions</th></tr></thead>
          <tbody>${list.map(e => `
            <tr>
              <td style="color:var(--muted)">${e.id}</td>
              <td><div style="font-weight:500;">${e.title}</div></td>
              <td>${formatDate(e.eventDate)}</td>
              <td style="color:var(--muted2)">${e.location || 'TBD'}</td>
              <td style="display:flex;gap:6px;">
                <button class="btn btn-ghost btn-sm" onclick="editEvent(${e.id},'${encodeURIComponent(JSON.stringify(e))}')">Edit</button>
                <button class="btn btn-danger btn-sm" onclick="deleteEvent(${e.id})">Delete</button>
              </td>
            </tr>
          `).join('')}</tbody>
        </table>
      </div>
    `;
  }).catch(e => toast(e.message, 'error'));
}

function renderAdminJobs() {
  api('/jobs').then(res => {
    const list = Array.isArray(res) ? res : (res.data || []);
    document.getElementById('admin-content').innerHTML = `
      <div class="table-wrap">
        <table>
          <thead><tr><th>ID</th><th>Title</th><th>Company</th><th>Description</th></tr></thead>
          <tbody>${list.map(j => `
            <tr>
              <td style="color:var(--muted)">${j.id}</td>
              <td><div style="font-weight:500;">${j.title}</div></td>
              <td style="color:var(--accent)">${j.company || '—'}</td>
              <td style="color:var(--muted2);max-width:300px;">${(j.description || '').substring(0, 80)}${j.description?.length > 80 ? '...' : ''}</td>
            </tr>
          `).join('')}</tbody>
        </table>
      </div>
    `;
  }).catch(e => toast(e.message, 'error'));
}

// ═══════════════════════════════════════════════════
// INIT
// ═══════════════════════════════════════════════════
updateNav();
